package com.example.springboot.config;

import com.example.springboot.servise.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.bootstrap-admins.enabled", havingValue = "true")
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);
    private static final int MIN_PASSWORD_LENGTH = 14;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admins.yeeee-password:}")
    private String yeeeePassword;

    @Value("${app.bootstrap-admins.chip-password:}")
    private String chipPassword;

    public AdminBootstrapRunner(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            validatePassword("yeeee", yeeeePassword);
            validatePassword("chip", chipPassword);
            if (yeeeePassword.equals(chipPassword)) {
                throw new IllegalStateException("两位管理员不能使用相同密码");
            }

            userService.upsertAdmin("yeeee", passwordEncoder.encode(yeeeePassword));
            userService.upsertAdmin("chip", passwordEncoder.encode(chipPassword));
            log.warn("管理员一次性引导完成；请立即删除密码环境变量并关闭 BOOTSTRAP_ADMINS_ENABLED");
        } finally {
            yeeeePassword = null;
            chipPassword = null;
        }
    }

    private void validatePassword(String account, String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalStateException(account + " 的引导密码至少需要14个字符");
        }
    }
}
