package org.emiloanwithbilltest.annotation;

import org.emiloanwithbill.annotations.PasswordValidator;
import org.emiloanwithbill.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserPasswordValidationTest {

    private User createUser(String password) {
        User u = new User();
        u.setUsername("anish");
        u.setPassword(password);
        return u;
    }

    @Test
    public void testValidPassword() {
        User u = createUser("Anish@2001");

        Assertions.assertDoesNotThrow(() -> PasswordValidator.validate(u));
    }

    @Test
    public void testMissingUppercase() {
        User u = createUser("anish@2001");  // no uppercase

        Exception ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate(u)
        );

        Assertions.assertTrue(ex.getMessage().toLowerCase().contains("password"));
    }

    @Test
    public void testMissingLowercase() {
        User u = createUser("ANISH@2001");

        Exception ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate(u)
        );

        Assertions.assertTrue(ex.getMessage().toLowerCase().contains("password"));
    }

    @Test
    public void testMissingDigit() {
        User u = createUser("Anish@xxxx");

        Exception ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate(u)
        );

        Assertions.assertTrue(ex.getMessage().toLowerCase().contains("password"));
    }

    @Test
    public void testMissingSpecialCharacter() {
        User u = createUser("Anish2001");

        Exception ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate(u)
        );

        Assertions.assertTrue(ex.getMessage().toLowerCase().contains("password"));
    }

    @Test
    public void testTooShort() {
        User u = createUser("Aa1@");  // less than 8 chars

        Exception ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PasswordValidator.validate(u)
        );

        Assertions.assertTrue(ex.getMessage().toLowerCase().contains("password"));
    }
}
