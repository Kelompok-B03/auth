package id.ac.ui.cs.gatherlove.auth.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    @Test
    public void testUserCreation() {
        User user = new User(
                "test@example.com",
                "hashedPassword",
                Set.of(Role.USER)
        );

        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedPassword", user.getPassword());
        assertTrue(user.getRoles().contains(Role.USER));
    }
}