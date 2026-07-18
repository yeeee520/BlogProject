package com.example.springboot.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    @Test
    void rejectsShortSecret() {
        JwtUtil jwtUtil = configured("too-short", 60_000);
        assertThrows(IllegalStateException.class, jwtUtil::validateConfiguration);
    }

    @Test
    void generatesUniqueTokensAndMatchesOnlyStoredHash() {
        JwtUtil jwtUtil = configured("0123456789abcdef0123456789abcdef0123456789abcdef", 60_000);
        jwtUtil.validateConfiguration();

        String first = jwtUtil.generateToken(1L, "admin-a");
        String second = jwtUtil.generateToken(1L, "admin-a");
        Claims claims = jwtUtil.parseToken(first);

        assertNotEquals(first, second);
        assertEquals(1L, claims.get("userId", Long.class));
        assertEquals("admin-a", claims.get("username", String.class));
        assertTrue(jwtUtil.matchesStoredToken(first, jwtUtil.hashToken(first)));
        assertTrue(!jwtUtil.matchesStoredToken(second, jwtUtil.hashToken(first)));
    }

    private JwtUtil configured(String secret, long expiration) {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
        return jwtUtil;
    }
}
