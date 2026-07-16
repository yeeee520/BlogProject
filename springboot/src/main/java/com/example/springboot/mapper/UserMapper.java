package com.example.springboot.mapper;

import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectManagerPage(@Param("userId") Long userId);
    User selectHeaderPage(@Param("userId") Long userId);

    User selectByName(@Param("account") String account);

    int insertUser(User user);

    User selectByUserId(@Param("userId") Long userId);
}