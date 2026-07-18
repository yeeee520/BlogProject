package com.example.springboot.controller;

import com.example.springboot.entity.User;
import com.example.springboot.service.LoginAttemptService;
import com.example.springboot.servise.UserService;
import com.example.springboot.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String DUMMY_PASSWORD_HASH =
            "$2a$12$srBmBF36CC3wCisNrEYkpupeMViumhryTk61EZTt.Jx5Y8GNS87bq";

    @Resource
    private UserService userService;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body,
                                                      HttpServletRequest request) {
        String clientKey = resolveClientIp(request);
        if (loginAttemptService.isBlocked(clientKey)) {
            return response(HttpStatus.TOO_MANY_REQUESTS, 429, "登录失败次数过多，请15分钟后重试", null);
        }

        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return response(HttpStatus.BAD_REQUEST, 400, "用户名和密码不能为空", null);
        }
        if (username.length() > 50 || password.length() > 200) {
            loginAttemptService.recordFailure(clientKey);
            return response(HttpStatus.BAD_REQUEST, 400, "用户名或密码格式错误", null);
        }

        User user = userService.findByName(username.trim());
        // 即使账号不存在也执行 BCrypt，降低通过响应耗时枚举管理员账号的风险。
        String storedHash = user == null ? DUMMY_PASSWORD_HASH : user.getPassword();
        boolean passwordMatches = storedHash != null && passwordEncoder.matches(password, storedHash);
        if (user == null || !ADMIN_ROLE.equals(user.getRole()) || !passwordMatches) {
            loginAttemptService.recordFailure(clientKey);
            return response(HttpStatus.UNAUTHORIZED, 401, "用户名或密码错误", null);
        }

        loginAttemptService.recordSuccess(clientKey);
        String token = jwtUtil.generateToken(user.getUserId(), user.getAccount());
        // 每个账号只保留最后一次登录生成的 Token 哈希。
        userService.updateToken(user.getUserId(), jwtUtil.hashToken(token));

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userData(user));
        return response(HttpStatus.OK, 200, "success", data);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> profile(Authentication authentication) {
        User user = currentUser(authentication);
        if (user == null) {
            return response(HttpStatus.UNAUTHORIZED, 401, "未登录", null);
        }
        return response(HttpStatus.OK, 200, "success", userData(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(Authentication authentication) {
        User user = currentUser(authentication);
        if (user != null) {
            userService.updateToken(user.getUserId(), null);
        }
        return response(HttpStatus.OK, 200, "已退出登录", null);
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Map<?, ?> principal)) {
            return null;
        }
        Object rawUserId = principal.get("userId");
        if (!(rawUserId instanceof Long userId)) {
            return null;
        }
        return userService.findByUserId(userId);
    }

    private Map<String, Object> userData(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getUserId());
        data.put("username", user.getAccount());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getPhoto());
        data.put("role", user.getRole());
        data.put("bio", "");
        data.put("followerCount", user.getFans());
        data.put("followingCount", user.getFocus());
        return data;
    }

    private String resolveClientIp(HttpServletRequest request) {
        // 生产环境由 Spring 的 ForwardedHeaderFilter 解析 Nginx 覆盖后的 X-Forwarded-For。
        // 后端只绑定 127.0.0.1，外网不能直接伪造该请求头。
        return request.getRemoteAddr();
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status, int code,
                                                          String message, Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", data);
        return ResponseEntity.status(status)
                .cacheControl(CacheControl.noStore())
                .body(body);
    }
}
