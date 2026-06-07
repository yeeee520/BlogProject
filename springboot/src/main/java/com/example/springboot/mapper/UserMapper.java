package com.example.springboot.mapper;

import com.example.springboot.entity.User;

import java.util.List;

public interface UserMapper {
    List<User> selectAll(User user);
}
