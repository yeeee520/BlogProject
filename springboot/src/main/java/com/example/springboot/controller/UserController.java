package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.servise.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    @GetMapping("/Manager")
    public Result managerUserData(@RequestParam Long userId){
        User user=userService.managerPage(userId);
        return Result.success(user);
    }

    @GetMapping("/header")
    public Result headerUserData(@RequestParam Long userId){
        User user=userService.headerPage(userId);
        return Result.success(user);
    }
}