package id.ac.ui.cs.gatherlove.auth.service;

import id.ac.ui.cs.gatherlove.auth.dto.JwtResponse;
import id.ac.ui.cs.gatherlove.auth.dto.LoginRequest;
import id.ac.ui.cs.gatherlove.auth.dto.RegisterRequest;
import id.ac.ui.cs.gatherlove.auth.model.Role;
import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.repository.RoleRepository;
import id.ac.ui.cs.gatherlove.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

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

        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setName(request.getName().trim());
        user.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null);
        user.setBio(request.getBio() != null ? request.getBio().trim() : null);
        user.setProfilePictureUrl(request.getProfilePictureUrl());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role donorRole = roleRepository.findByName("DONOR")
                .orElseThrow(() -> new RuntimeException("Role not found: DONOR"));

        user.getRoles().add(donorRole);

        userRepository.save(user);

        // TODO: CREATE WALLET
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


    public JwtResponse login(LoginRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required");
            }

            // Clean email input
            String email = request.getEmail().trim();

            // First check if user exists
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

            // Then attempt authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );

            // Create claims map
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", user.getEmail());
            claims.put("userId", user.getId());
            claims.put("roles", user.getRoles().stream().map(Role::getName).toList());

            // Generate JWT token
            String token = jwtService.generateJWT(claims);

            return new JwtResponse(token);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password", e);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed", e);
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }
}