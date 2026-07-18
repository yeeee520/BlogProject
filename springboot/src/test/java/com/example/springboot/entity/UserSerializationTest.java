package com.example.springboot.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserSerializationTest {

    @Test
    void neverSerializesPasswordOrToken() throws Exception {
        User user = new User();
        user.setAccount("admin-a");
        user.setPassword("sensitive-password");
        user.setToken("sensitive-token");

        String json = new ObjectMapper().writeValueAsString(user);

        assertTrue(json.contains("admin-a"));
        assertFalse(json.contains("password"));
        assertFalse(json.contains("token"));
        assertFalse(json.contains("sensitive"));
    }
}
