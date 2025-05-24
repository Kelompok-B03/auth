package id.ac.ui.cs.gatherlove.auth.controller;

import id.ac.ui.cs.gatherlove.auth.model.Role;
import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> safeUsers = users.stream().map(this::mapUserToSafeResponse).collect(Collectors.toList());
        return ResponseEntity.ok(safeUsers);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userService.loadUserById(#userId).email == authentication.name")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable UUID userId) {
        try {
            User user = userService.loadUserById(userId);
            return ResponseEntity.ok(mapUserToSafeResponse(user));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #email == authentication.name")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.loadUserByEmail(email);
            return ResponseEntity.ok(mapUserToSafeResponse(user));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{userId}/block")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> blockUser(@PathVariable UUID userId) {
        try {
            userService.blockUser(userId);
            return ResponseEntity.ok(Map.of("message", "User " + userId + " blocked successfully."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{userId}/unblock")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> unblockUser(@PathVariable UUID userId) {
        try {
            userService.unblockUser(userId);
            return ResponseEntity.ok(Map.of("message", "User " + userId + " unblocked successfully."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Long>> getTotalUsers() {
        long totalUsers = userService.getTotalUsers();
        return ResponseEntity.ok(Map.of("totalUsers", totalUsers));
    }


    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong");
    }

    private Map<String, Object> mapUserToSafeResponse(User user) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("id", user.getId().toString());
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("bio", user.getBio());
        response.put("profilePictureUrl", user.getProfilePictureUrl());
        response.put("walletId", user.getWalletId());
        response.put("isActive", user.isActive());
        response.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        response.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        response.put("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
        return response;
    }
}