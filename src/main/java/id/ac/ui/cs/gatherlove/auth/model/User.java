package id.ac.ui.cs.gatherlove.auth.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    private LocalDateTime createdAt;

    private boolean isActive = true;

    private Long walletId;

    private LocalDateTime updatedAt;

    private String name;

    private String phoneNumber;

    @Nullable
    @Column(nullable = true)
    private String profilePictureUrl;

    @Nullable
    @Column(nullable = true)
    private String bio;

    private User(Builder builder) {
        this.email = builder.email;
        this.password = builder.password;
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.profilePictureUrl = builder.profilePictureUrl;
        this.bio = builder.bio;
        this.walletId = builder.walletId;
        if (builder.roles != null) {
            this.roles = builder.roles;
        }
        this.isActive = builder.isActive;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Builder builder(String email, String password) {
        return new Builder(email, password);
    }

    public static class Builder {
        // Required parameters
        private final String email;
        private final String password;

        // Optional parameters
        private String name;
        private String phoneNumber;
        private String profilePictureUrl;
        private String bio;
        private Long walletId;
        private Set<Role> roles = new HashSet<>();
        private boolean isActive = true;

        // Builder constructor for required fields
        private Builder(String email, String password) {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }
            this.email = email;
            this.password = password;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder profilePictureUrl(@Nullable String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public Builder bio(@Nullable String bio) {
            this.bio = bio;
            return this;
        }

        public Builder walletId(Long walletId) {
            this.walletId = walletId;
            return this;
        }

        public Builder roles(Set<Role> roles) {
            if (roles != null) {
                this.roles = roles;
            }
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
