package com.example.springboot.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptServiceTest {

    @Test
    void blocksAfterFiveFailuresAndSuccessClearsState() {
        LoginAttemptService service = new LoginAttemptService();
        String client = "127.0.0.1";

        for (int i = 0; i < 4; i++) {
            service.recordFailure(client);
            assertFalse(service.isBlocked(client));
        }
        service.recordFailure(client);
        assertTrue(service.isBlocked(client));

        service.recordSuccess(client);
        assertFalse(service.isBlocked(client));
    }
}
