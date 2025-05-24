package id.ac.ui.cs.gatherlove.auth.service;

import id.ac.ui.cs.gatherlove.auth.dto.JwtResponse;
import id.ac.ui.cs.gatherlove.auth.dto.LoginRequest;
import id.ac.ui.cs.gatherlove.auth.dto.RegisterRequest;
import id.ac.ui.cs.gatherlove.auth.model.Role;
import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.repository.RoleRepository;
import id.ac.ui.cs.gatherlove.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Role donorRole;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setName("John Doe");
        user.setId(new UUID(0,0));

        donorRole = new Role();
        donorRole.setName("DONOR");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setName("John Doe");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void testRegisterAsDonorEmailAlreadyInUse() {
        // Arrange
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> authService.registerAsDonor(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already in use");
    }

    @Test
    void testRegisterAsDonorMissingEmail() {
        // Arrange
        registerRequest.setEmail(null);

        // Act & Assert
        assertThatThrownBy(() -> authService.registerAsDonor(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is required");
    }

//    @Test
//    void testUpgradeToFundraiserSuccess() {
//        // Arrange
//        user.setRoles(Set.of(donorRole));
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//        when(roleRepository.findByName("FUNDRAISER")).thenReturn(Optional.of(new Role()));
//
//        // Act
//        authService.upgradeToFundraiser(user.getEmail());
//
//        // Assert
//        verify(userRepository).save(user);
//    }

    @Test
    void testUpgradeToFundraiserUserAlreadyFundraiser() {
        // Arrange
        Role fundraiserRole = new Role();
        fundraiserRole.setName("FUNDRAISER");
        user.setRoles(Set.of(fundraiserRole));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> authService.upgradeToFundraiser(user.getEmail()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User is already a fundraiser");
    }

    @Test
    void testUpgradeToFundraiserRoleNotFound() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(roleRepository.findByName("FUNDRAISER")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.upgradeToFundraiser(user.getEmail()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Role not found: FUNDRAISER");
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        when(authenticationManager.authenticate(any())).thenReturn(null); // Mock successful authentication
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateJWT(any())).thenReturn("token");

        // Act
        JwtResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response.getToken()).isEqualTo("token");
    }
}
