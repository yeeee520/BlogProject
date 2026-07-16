package com.example.springboot.servise;

import com.example.springboot.entity.User;
import com.example.springboot.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Resource
    UserMapper userMapper;

    public User managerPage(Long userId){
        return userMapper.selectManagerPage(userId);
    }

    public User headerPage(Long userId){
        return userMapper.selectHeaderPage(userId);
    }

    public User findByName(String account) {
        return userMapper.selectByName(account);
    }

    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    public User findByUserId(Long userId) {
        return userMapper.selectByUserId(userId);
    }
}