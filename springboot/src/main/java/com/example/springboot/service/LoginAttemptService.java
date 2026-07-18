package com.example.springboot.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_FAILURES = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);
    private final ConcurrentHashMap<String, AttemptState> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String clientKey) {
        AttemptState state = attempts.get(clientKey);
        if (state == null) {
            return false;
        }
        synchronized (state) {
            if (state.windowStarted.plus(WINDOW).isBefore(Instant.now())) {
                attempts.remove(clientKey, state);
                return false;
            }
            return state.failures >= MAX_FAILURES;
        }
    }

    public void recordFailure(String clientKey) {
        attempts.compute(clientKey, (key, current) -> {
            Instant now = Instant.now();
            if (current == null || current.windowStarted.plus(WINDOW).isBefore(now)) {
                return new AttemptState(now, 1);
            }
            synchronized (current) {
                current.failures++;
                return current;
            }
        });
    }

    public void recordSuccess(String clientKey) {
        attempts.remove(clientKey);
    }

    private static final class AttemptState {
        private final Instant windowStarted;
        private int failures;

        private AttemptState(Instant windowStarted, int failures) {
            this.windowStarted = windowStarted;
            this.failures = failures;
        }
    }
}
