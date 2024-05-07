package edu.usc.csci310.project.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExceptionTest {
    @Test
    void testEmailAlreadyInUseException() {
        final String message = "Email already in use";
        Exception exception = Assertions.assertThrows(EmailAlreadyInUseException.class, () -> {
            throw new EmailAlreadyInUseException(message);
        });

        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    void testUserNotFoundException() {
        final String message = "User not found";
        Exception exception = Assertions.assertThrows(UserNotFoundException.class, () -> {
            throw new UserNotFoundException(message);
        });

        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    void testInvalidPasswordException() {
        final String message = "Invalid password";
        Exception exception = Assertions.assertThrows(InvalidPasswordException.class, () -> {
            throw new InvalidPasswordException(message);
        });

        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    void testAccountLockedException() {
        final String message = "Invalid password";
        Exception exception = Assertions.assertThrows(AccountLockedException.class, () -> {
            throw new AccountLockedException(message);
        });

        Assertions.assertEquals(message, exception.getMessage());
    }
}
