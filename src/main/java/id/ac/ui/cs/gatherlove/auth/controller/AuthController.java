package id.ac.ui.cs.gatherlove.auth.controller;

import id.ac.ui.cs.gatherlove.auth.dto.response.JwtResponse;
import id.ac.ui.cs.gatherlove.auth.dto.request.LoginRequest;
import id.ac.ui.cs.gatherlove.auth.dto.request.PromoteAdminRequest;
import id.ac.ui.cs.gatherlove.auth.dto.request.RegisterRequest;
import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.service.AuthService;
import id.ac.ui.cs.gatherlove.auth.service.JwtService;
import id.ac.ui.cs.gatherlove.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;
    private final UserService userService;

    @PostMapping(path = "/token", consumes = APPLICATION_JSON_VALUE)
    public String getToken(@RequestBody Map<String, Object> claims) {
        return jwtService.generateJWT(claims);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            authService.registerAsDonor(request);
            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully as DONOR",
                    "status", "success"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage(),
                    "status", "error"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", e.getMessage(),
                    "status", "error"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "An unexpected error occurred during registration",
                    "status", "error"
            ));
        }
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade(Authentication authentication) {
        try {
            if (!(authentication instanceof JwtAuthenticationToken jwtToken)) {
                throw new RuntimeException("Invalid authentication token");
            }

            String email = jwtToken.getToken().getClaimAsString("email");

            if (email == null || email.isEmpty()) {
                throw new RuntimeException("Email not found in token");
            }

            // Upgrade user by email
            authService.upgradeToFundraiser(email);

            return ResponseEntity.ok(Map.of(
                    "message", "User upgraded to FUNDRAISER successfully",
                    "status", "success"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage(),
                    "status", "error"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "An unexpected error occurred during upgrade",
                    "status", "error"
            ));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", e.getMessage(),
                    "status", "error"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "An unexpected error occurred during login",
                    "status", "error"
            ));
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.loadUserByEmail(email);
            return ResponseEntity.ok(Map.of(
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "roles", user.getRoles().stream().map(role -> role.getName()).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "User not found",
                    "status", "error"
            ));
        }
    }

    /**
     * Endpoint untuk mempromosikan pengguna menjadi ADMIN.
     * Hanya dapat diakses oleh pengguna yang sudah memiliki role ADMIN.
     * @param request Berisi email pengguna yang akan dipromosikan.
     * @return ResponseEntity yang mengindikasikan sukses atau gagal.
     */
    @PostMapping("/admin/promote")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> promoteToAdmin(@Valid @RequestBody PromoteAdminRequest request) {
        try {
            authService.promoteToAdmin(request.getEmail().trim());
            return ResponseEntity.ok(Map.of(
                    "message", "User " + request.getEmail() + " promoted to ADMIN successfully",
                    "status", "success"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage(),
                    "status", "error"
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage(), "status", "error"));
            } else if (e.getMessage().toLowerCase().contains("already an admin")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage(), "status", "error"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage(),
                    "status", "error"
            ));
        } catch (Exception e) {
            System.err.println("Unexpected error during admin promotion: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "An unexpected error occurred during admin promotion",
                    "status", "error"
            ));
        }
    }
}