package id.ac.ui.cs.gatherlove.auth.controller;

import id.ac.ui.cs.gatherlove.auth.dto.response.PublicUserProfileResponse;
import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/profiles")
public class PublicProfileController {

    private final UserService userService;

    public PublicProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPublicProfileById(@PathVariable UUID userId) {
        try {
            User user = userService.loadUserById(userId);
            if (!user.isActive()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User profile not found or not active.", "status", "error"));
            }
            return ResponseEntity.ok(PublicUserProfileResponse.fromUser(user));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User profile not found.", "status", "error"));
        } catch (Exception e) {
            System.err.println("Error fetching public profile for user " + userId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred.", "status", "error"));
        }
    }
}