package com.example.springboot.config;

import com.example.springboot.servise.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AdminBootstrapRunnerTest {

    @Test
    void hashesAndUpsertsTwoDifferentStrongPasswords() {
        UserService userService = mock(UserService.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AdminBootstrapRunner runner = new AdminBootstrapRunner(userService, encoder);
        ReflectionTestUtils.setField(runner, "yeeeePassword", "first-strong-password");
        ReflectionTestUtils.setField(runner, "chipPassword", "second-strong-password");
        when(encoder.encode("first-strong-password")).thenReturn("hash-one");
        when(encoder.encode("second-strong-password")).thenReturn("hash-two");

        runner.run(new DefaultApplicationArguments());

        verify(userService).upsertAdmin("yeeee", "hash-one");
        verify(userService).upsertAdmin("chip", "hash-two");
    }

    @Test
    void rejectsShortOrReusedPasswords() {
        UserService userService = mock(UserService.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AdminBootstrapRunner shortPasswordRunner = new AdminBootstrapRunner(userService, encoder);
        ReflectionTestUtils.setField(shortPasswordRunner, "yeeeePassword", "too-short");
        ReflectionTestUtils.setField(shortPasswordRunner, "chipPassword", "second-strong-password");

        assertThrows(IllegalStateException.class,
                () -> shortPasswordRunner.run(new DefaultApplicationArguments()));
        verifyNoInteractions(userService, encoder);

        AdminBootstrapRunner reusedPasswordRunner = new AdminBootstrapRunner(userService, encoder);
        ReflectionTestUtils.setField(reusedPasswordRunner, "yeeeePassword", "same-strong-password");
        ReflectionTestUtils.setField(reusedPasswordRunner, "chipPassword", "same-strong-password");
        assertThrows(IllegalStateException.class,
                () -> reusedPasswordRunner.run(new DefaultApplicationArguments()));
        verifyNoInteractions(userService, encoder);
    }
}
