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

    int updateToken(@Param("userId") Long userId, @Param("token") String token);

    int upsertAdmin(@Param("account") String account, @Param("password") String password);
}
