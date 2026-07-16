package com.example.springboot.config;

import com.example.springboot.entity.User;
import com.example.springboot.servise.UserService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitRunner implements CommandLineRunner {

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User admin = userService.findByName("admin");
        if (admin == null) {
            User user = new User();
            user.setAccount("admin");
            user.setNickname("旅行者");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setFans(0);
            user.setFocus(0);
            userService.insertUser(user);
            System.out.println(">>> Admin user created: admin / 123456");
        }
    }
}