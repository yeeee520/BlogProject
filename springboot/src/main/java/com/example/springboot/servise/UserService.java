package com.example.springboot.servise;


import com.example.springboot.entity.User;
import com.example.springboot.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Resource
    UserMapper userMapper;
    //首页显示
    public User managerPage(Integer userId){
        return userMapper.selectManagerPage(userId);
    }
    //其他页面显示
    public User headerPage(Integer userId){
        return userMapper.selectHeaderPage(userId);
    }
}
