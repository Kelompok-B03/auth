package id.ac.ui.cs.gatherlove.auth.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testNoArgsConstructor() {
        assertThat(user).isNotNull();
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String password = "secret";
        Set<Role> roles = new HashSet<>();
        LocalDateTime createdAt = LocalDateTime.now();
        boolean isActive = true;
        String walletId = "wallet123";
        LocalDateTime updatedAt = LocalDateTime.now();
        String name = "Test User";
        String phoneNumber = "123456789";
        String profilePictureUrl = "http://example.com/pic.jpg";
        String bio = "Bio info";

        User newUser = new User(id, email, password, roles, createdAt, isActive,
                walletId, updatedAt, name, phoneNumber, profilePictureUrl, bio);

        assertThat(newUser.getId()).isEqualTo(id);
        assertThat(newUser.getEmail()).isEqualTo(email);
        assertThat(newUser.getPassword()).isEqualTo(password);
        assertThat(newUser.getRoles()).isEqualTo(roles);
        assertThat(newUser.getCreatedAt()).isEqualTo(createdAt);
        assertThat(newUser.isActive()).isEqualTo(isActive);
        assertThat(newUser.getWalletId()).isEqualTo(walletId);
        assertThat(newUser.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(newUser.getName()).isEqualTo(name);
        assertThat(newUser.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(newUser.getProfilePictureUrl()).isEqualTo(profilePictureUrl);
        assertThat(newUser.getBio()).isEqualTo(bio);
    }

    @Test
    void testSettersAndGetters() {
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setEmail("user@example.com");
        user.setPassword("pass123");
        user.setActive(false);
        user.setWalletId("wallet123");
        user.setCreatedAt(LocalDateTime.of(2023, 1, 1, 10, 0));
        user.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        user.setName("User Name");
        user.setPhoneNumber("9876543210");
        user.setProfilePictureUrl("http://example.com/profile.jpg");
        user.setBio("User bio");

        Role role = new Role();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getPassword()).isEqualTo("pass123");
        assertThat(user.isActive()).isFalse();
        assertThat(user.getWalletId()).isEqualTo("wallet123");
        assertThat(user.getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 1, 1, 10, 0));
        assertThat(user.getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(user.getName()).isEqualTo("User Name");
        assertThat(user.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(user.getProfilePictureUrl()).isEqualTo("http://example.com/profile.jpg");
        assertThat(user.getBio()).isEqualTo("User bio");
        assertThat(user.getRoles()).contains(role);
    }

    @Test
    void testOnCreate() {
        user.onCreate();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
    }

    @Test
    void testOnUpdate() {
        user.setUpdatedAt(LocalDateTime.of(2023, 1, 1, 10, 0));
        user.onUpdate();
        assertThat(user.getUpdatedAt()).isAfter(LocalDateTime.of(2023, 1, 1, 10, 0));
    }
}
