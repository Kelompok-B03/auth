package id.ac.ui.cs.gatherlove.auth.service;

import id.ac.ui.cs.gatherlove.auth.model.Role;
import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setActive(true);
        user.setRoles(new HashSet<>());
        user.setId(new UUID(0,0));
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        // Assert
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void testLoadUserByUsernameThrowsExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.loadUserByUsername(user.getEmail()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: " + user.getEmail());
    }

    @Test
    void testGetAuthorities() {
        // Arrange
        Role role = new Role();
        role.setName("USER");
        Set<Role> roles = Set.of(role);

        // Act
        Set<GrantedAuthority> authorities = userService.getAuthorities(roles);

        // Assert
        assertThat(authorities).isNotEmpty();
        assertThat(authorities).contains(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Test
    void testLoadUserByIdSuccess() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.loadUserById(user.getId());

        // Assert
        assertThat(foundUser).isEqualTo(user);
        verify(userRepository).findById(user.getId());
    }

    @Test
    void testLoadUserByIdThrowsExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.loadUserById(user.getId()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with id: " + user.getId());
    }

    @Test
    void testLoadUserByEmailSuccess() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.loadUserByEmail(user.getEmail());

        // Assert
        assertThat(foundUser).isEqualTo(user);
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void testLoadUserByEmailThrowsExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.loadUserByEmail(user.getEmail()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: " + user.getEmail());
    }
}