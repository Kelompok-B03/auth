package id.ac.ui.cs.gatherlove.auth.service;

import id.ac.ui.cs.gatherlove.auth.dto.response.JwtResponse;
import id.ac.ui.cs.gatherlove.auth.dto.request.LoginRequest;
import id.ac.ui.cs.gatherlove.auth.dto.request.RegisterRequest;
import id.ac.ui.cs.gatherlove.auth.model.Role;
import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.repository.RoleRepository;
import id.ac.ui.cs.gatherlove.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final WebClient.Builder webClientBuilder;

    @Value("${wallet.service.baseurl}")
    private String walletServiceBaseUrl;

    @Transactional
    public void registerAsDonor(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (userRepository.findByEmail(request.getEmail().trim()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Role donorRole = roleRepository.findByName("DONOR")
                .orElseThrow(() -> new RuntimeException("Role not found: DONOR"));

        Set<Role> roles = new HashSet<>();
        roles.add(donorRole);

        User user = User.builder(request.getEmail(), encodedPassword)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null)
                .bio(request.getBio() != null ? request.getBio().trim() : null)
                .profilePictureUrl(request.getProfilePictureUrl())
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);

        try {
            Long walletId = createWalletForUser(savedUser.getId());
            user.setWalletId(walletId);
            System.out.println("Successfully created wallet with ID: " + walletId + " for user: " + savedUser.getId());
        } catch (Exception e) {
            System.err.println("Failed to create wallet for user " + savedUser.getId() + ": " + e.getMessage());
            throw new RuntimeException("User registration failed because wallet creation failed: " + e.getMessage(), e);
        }    }

    private Long createWalletForUser(UUID userId) {
        WebClient webClient = webClientBuilder.baseUrl(walletServiceBaseUrl).build();
        System.out.println("Attempting to create wallet for userId: " + userId + " at URL: " + walletServiceBaseUrl + "/api/wallet");

        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/api/wallet")
                            .queryParam("userId", userId.toString())
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("[No error body from server]")
                                .flatMap(errorBody -> {
                                    String errorMessage = "Wallet service call failed with status " + clientResponse.statusCode() + ". Response: " + errorBody;
                                    System.err.println(errorMessage);
                                    return Mono.error(new RuntimeException(errorMessage));
                                });
                    })
                    .bodyToMono(Long.class)
                    .block();

        } catch (RuntimeException e) {
            System.err.println("Exception during WebClient call in createWalletForUser: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Upgrades a user to fundraiser role using an email
     */
    @Transactional
    public void upgradeToFundraiser(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("User roles before upgrade: " + user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", ")));

        if (user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("FUNDRAISER"))) {
            throw new RuntimeException("User is already a fundraiser");
        }

        Role fundraiserRole = roleRepository.findByName("FUNDRAISER")
                .orElseThrow(() -> new RuntimeException("Role not found: FUNDRAISER"));

        user.getRoles().add(fundraiserRole);

        userRepository.save(user);

        System.out.println("User roles after upgrade: " + user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", ")));
    }

    /**
     * Upgrades a user to admin role using an email
     */
    @Transactional
    public void promoteToAdmin(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role ADMIN not found. Please ensure it is initialized."));

        if (user.getRoles().contains(adminRole)) {
            throw new RuntimeException("User is already an ADMIN.");
        }

        user.getRoles().add(adminRole);
        userRepository.save(user);
        System.out.println("User " + userEmail + " has been promoted to ADMIN.");
    }


    public JwtResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        String email = request.getEmail().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isActive()) {
            throw new DisabledException("User account is blocked.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );

            Map<String, Object> claims = new HashMap<>();
            claims.put("email", user.getEmail());
            claims.put("userId", user.getId().toString());
            claims.put("roles", user.getRoles().stream().map(Role::getName).toList());

            String token = jwtService.generateJWT(claims);

            return new JwtResponse(token);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password", e);
        } catch (DisabledException e) {
            throw new RuntimeException("User account is blocked.", e);
        } catch (AuthenticationException e) {
            if (e.getMessage() != null && e.getMessage().contains("User account is blocked")) {
                throw new RuntimeException("User account is blocked.", e);
            }
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Unexpected error during login for user " + email + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Login failed due to an unexpected error.", e);
        }
    }
}