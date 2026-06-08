package com.example.springboot.mapper;

import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    User selectManagerPage(Integer userId);
    User selectHeaderPage(Integer userId);
}
