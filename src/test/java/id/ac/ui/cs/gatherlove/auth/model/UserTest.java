package id.ac.ui.cs.gatherlove.auth.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserTest {

    private User user;
    private final String defaultEmail = "test@example.com";
    private final String defaultPassword = "password123";
    private final String defaultName = "Test User";

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testNoArgsConstructor() {
        User newUser = new User();
        assertThat(newUser).isNotNull();
        assertThat(newUser.isActive()).isTrue();
        assertThat(newUser.getRoles()).isNotNull().isEmpty();
    }

    @Test
    void testBuilderMinimal() {
        User builtUser = User.builder(defaultEmail, defaultPassword).build();

        assertThat(builtUser.getEmail()).isEqualTo(defaultEmail);
        assertThat(builtUser.getPassword()).isEqualTo(defaultPassword);
        assertThat(builtUser.getName()).isNull();
        assertThat(builtUser.getRoles()).isNotNull().isEmpty();
        assertThat(builtUser.isActive()).isTrue();
    }

    @Test
    void testBuilderFull() {
        Set<Role> roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setName("USER");
        roles.add(userRole);

        String phoneNumber = "123456789";
        String profilePictureUrl = "http://example.com/pic.jpg";
        String bio = "This is a bio.";
        Long walletId = 123L;
        boolean isActive = false;

        User builtUser = User.builder(defaultEmail, defaultPassword)
                .name(defaultName)
                .phoneNumber(phoneNumber)
                .profilePictureUrl(profilePictureUrl)
                .bio(bio)
                .roles(roles)
                .walletId(walletId)
                .isActive(isActive)
                .build();

        assertThat(builtUser.getEmail()).isEqualTo(defaultEmail);
        assertThat(builtUser.getPassword()).isEqualTo(defaultPassword);
        assertThat(builtUser.getName()).isEqualTo(defaultName);
        assertThat(builtUser.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(builtUser.getProfilePictureUrl()).isEqualTo(profilePictureUrl);
        assertThat(builtUser.getBio()).isEqualTo(bio);
        assertThat(builtUser.getRoles()).isEqualTo(roles);
        assertThat(builtUser.getWalletId()).isEqualTo(walletId);
        assertThat(builtUser.isActive()).isEqualTo(isActive);

        assertThat(builtUser.getCreatedAt()).isNull();
        assertThat(builtUser.getUpdatedAt()).isNull();
    }

    @Test
    void testBuilderThrowsExceptionForNullEmail() {
        assertThatThrownBy(() -> User.builder(null, defaultPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null or empty");
    }

    @Test
    void testBuilderThrowsExceptionForEmptyEmail() {
        assertThatThrownBy(() -> User.builder(" ", defaultPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null or empty");
    }

    @Test
    void testBuilderThrowsExceptionForNullPassword() {
        assertThatThrownBy(() -> User.builder(defaultEmail, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be null or empty");
    }

    @Test
    void testBuilderThrowsExceptionForEmptyPassword() {
        assertThatThrownBy(() -> User.builder(defaultEmail, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be null or empty");
    }


    @Test
    void testSettersAndGetters() {
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setEmail("user@example.com");
        user.setPassword("pass123");
        user.setActive(false);
        user.setWalletId(99L);
        user.setName("User Name");
        user.setPhoneNumber("9876543210");
        user.setProfilePictureUrl("http://example.com/profile.jpg");
        user.setBio("User bio");

        LocalDateTime manualCreatedAt = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime manualUpdatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        user.setCreatedAt(manualCreatedAt);
        user.setUpdatedAt(manualUpdatedAt);


        Role role = new Role();
        role.setName("TEST_ROLE");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getPassword()).isEqualTo("pass123");
        assertThat(user.isActive()).isFalse();
        assertThat(user.getWalletId()).isEqualTo(99L);
        assertThat(user.getCreatedAt()).isEqualTo(manualCreatedAt);
        assertThat(user.getUpdatedAt()).isEqualTo(manualUpdatedAt);
        assertThat(user.getName()).isEqualTo("User Name");
        assertThat(user.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(user.getProfilePictureUrl()).isEqualTo("http://example.com/profile.jpg");
        assertThat(user.getBio()).isEqualTo("User bio");
        assertThat(user.getRoles()).contains(role);
    }

    @Test
    void testOnCreate() {
        User newUser = User.builder(defaultEmail, defaultPassword).build();
        newUser.onCreate();

        assertThat(newUser.getCreatedAt()).isNotNull();
        assertThat(newUser.getUpdatedAt()).isNotNull();
        assertThat(newUser.getUpdatedAt()).isEqualTo(newUser.getCreatedAt());
    }

    @Test
    void testOnUpdate() {
        User newUser = User.builder(defaultEmail, defaultPassword).build();
        newUser.onCreate();

        LocalDateTime initialUpdatedAt = newUser.getUpdatedAt();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        newUser.onUpdate();

        assertThat(newUser.getUpdatedAt()).isNotNull();
        assertThat(newUser.getUpdatedAt()).isAfter(initialUpdatedAt);
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        User user1 = User.builder("user1@example.com", "pass1")
                .name("User One")
                .build();
        user1.setId(id);

        User user2 = User.builder("user1@example.com", "pass1")
                .name("User One")
                .build();
        user2.setId(id);

        User user3 = User.builder("user2@example.com", "pass2")
                .name("User Two")
                .build();
        user3.setId(UUID.randomUUID());

        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1).isEqualTo(user1);
        assertThat(user1).isNotEqualTo(null);
        assertThat(user1).isNotEqualTo(new Object());
    }
}