package com.example.springboot.controller;

import com.example.springboot.entity.User;
import com.example.springboot.servise.UserService;
import com.example.springboot.utils.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Resource
    private UserService userService;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Map<String, Object> result = new HashMap<>();

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            result.put("code", 400);
            result.put("message", "用户名和密码不能为空");
            result.put("data", null);
            return result;
        }

        User user = userService.findByName(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
            result.put("data", null);
            return result;
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getAccount());

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getUserId());
        userData.put("username", user.getAccount());
        userData.put("nickname", user.getNickname());
        userData.put("avatar", user.getPhoto());
        userData.put("bio", "");

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userData);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        String account = body.get("username");
        String password = body.get("password");
        String nickname = body.get("nickname");

        Map<String, Object> result = new HashMap<>();

        if (account == null || password == null || account.isEmpty() || password.isEmpty()) {
            result.put("code", 400);
            result.put("message", "用户名和密码不能为空");
            result.put("data", null);
            return result;
        }

        if (password.length() < 6) {
            result.put("code", 400);
            result.put("message", "密码长度不能少于6位");
            result.put("data", null);
            return result;
        }

        if (nickname == null || nickname.isEmpty()) {
            nickname = account;
        }

        User existing = userService.findByName(account);
        if (existing != null) {
            result.put("code", 400);
            result.put("message", "用户名已存在");
            result.put("data", null);
            return result;
        }

        User user = new User();
        user.setAccount(account);
        user.setNickname(nickname);
        user.setPassword(passwordEncoder.encode(password));
        user.setFans(0);
        user.setFocus(0);

        userService.insertUser(user);

        String token = jwtUtil.generateToken(user.getUserId(), user.getAccount());

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getUserId());
        userData.put("username", user.getAccount());
        userData.put("nickname", user.getNickname());
        userData.put("avatar", user.getPhoto());
        userData.put("bio", "");

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userData);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    @GetMapping("/profile")
    public Map<String, Object> profile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            result.put("code", 401);
            result.put("message", "未登录");
            result.put("data", null);
            return result;
        }

        try {
            String token = authHeader.substring(7);
            var claims = jwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            User user = userService.findByUserId(userId);

            if (user == null) {
                result.put("code", 404);
                result.put("message", "用户不存在");
                result.put("data", null);
                return result;
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getUserId());
            userData.put("username", user.getAccount());
            userData.put("nickname", user.getNickname());
            userData.put("avatar", user.getPhoto());
            userData.put("bio", "");
            userData.put("postCount", 0);
            userData.put("followerCount", user.getFans());
            userData.put("followingCount", user.getFocus());

            result.put("code", 200);
            result.put("message", "success");
            result.put("data", userData);
            return result;
        } catch (Exception e) {
            result.put("code", 401);
            result.put("message", "Token无效");
            result.put("data", null);
            return result;
        }
    }
}