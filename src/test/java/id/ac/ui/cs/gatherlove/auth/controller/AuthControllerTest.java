package id.ac.ui.cs.gatherlove.auth.controller;

import id.ac.ui.cs.gatherlove.auth.dto.JwtResponse;
import id.ac.ui.cs.gatherlove.auth.dto.LoginRequest;
import id.ac.ui.cs.gatherlove.auth.dto.RegisterRequest;
import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.service.AuthService;
import id.ac.ui.cs.gatherlove.auth.service.JwtService;
import id.ac.ui.cs.gatherlove.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setName("John Doe");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void testGetToken() throws Exception {
        // Arrange
        Map<String, Object> claims = Map.of("email", "test@example.com", "roles", "USER");
        when(jwtService.generateJWT(claims)).thenReturn("jwt-token");

        // Act & Assert
        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"roles\":\"USER\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));
    }

    @Test
    void testRegisterSuccess() throws Exception {
        // Arrange
        doNothing().when(authService).registerAsDonor(any(RegisterRequest.class));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password123\", \"name\":\"John Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully as DONOR"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testRegisterEmailAlreadyInUse() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Email already in use")).when(authService).registerAsDonor(any(RegisterRequest.class));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password123\", \"name\":\"John Doe\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already in use"))
                .andExpect(jsonPath("$.status").value("error"));
    }

//    @Test
//    void testUpgradeSuccess() throws Exception {
//        // Arrange
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn("test@example.com");
//        doNothing().when(authService).upgradeToFundraiser(anyString());
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/upgrade")
//                        .principal(authentication))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("User upgraded to FUNDRAISER successfully"))
//                .andExpect(jsonPath("$.status").value("success"));
//    }

//    @Test
//    void testUpgradeError() throws Exception {
//        // Arrange
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn("test@example.com");
//        doThrow(new RuntimeException("User not found")).when(authService).upgradeToFundraiser(anyString());
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/upgrade")
//                        .principal(authentication))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("User not found"))
//                .andExpect(jsonPath("$.status").value("error"));
//    }

    @Test
    void testLoginSuccess() throws Exception {
        // Arrange
        JwtResponse jwtResponse = new JwtResponse("jwt-token");
        when(authService.login(any(LoginRequest.class))).thenReturn(jwtResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Invalid email or password")).when(authService).login(any(LoginRequest.class));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void testGetCurrentUserNotFound() throws Exception {
        // Arrange
        when(userService.loadUserByEmail(anyString())).thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/auth/me")
                        .principal(mock(Authentication.class)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.status").value("error"));
    }
}