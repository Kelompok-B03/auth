package id.ac.ui.cs.gatherlove.auth.service;

import id.ac.ui.cs.gatherlove.auth.model.Role;
import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import id.ac.ui.cs.gatherlove.auth.dto.request.UpdateUserProfileRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is blocked: " + email);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user.getRoles()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public Set<GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());
    }

    public User loadUserById(UUID id) throws UsernameNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    public User loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    @Transactional
    public User blockUser(UUID userId) throws UsernameNotFoundException {
        User user = loadUserById(userId);
        if (!user.isActive()) {
            throw new IllegalStateException("User with ID " + userId + " is already blocked.");
        }
        user.setActive(false);
        return userRepository.save(user);
    }

    @Transactional
    public User unblockUser(UUID userId) throws UsernameNotFoundException {
        User user = loadUserById(userId);
        if (user.isActive()) {
            throw new IllegalStateException("User with ID " + userId + " is already active.");
        }
        user.setActive(true);
        return userRepository.save(user);
    }

    /**
     * Memperbarui profil pengguna.
     * @param userId ID pengguna yang akan diperbarui.
     * @param request DTO yang berisi data pembaruan.
     * @param authenticatedUserEmail Email pengguna yang terautentikasi (untuk verifikasi kepemilikan).
     * @return Pengguna yang telah diperbarui.
     * @throws UsernameNotFoundException jika pengguna tidak ditemukan.
     * @throws SecurityException jika pengguna mencoba memperbarui profil orang lain (kecuali admin).
     */
    @Transactional
    public User updateUserProfile(UUID userId, UpdateUserProfileRequest request, String authenticatedUserEmail)
            throws UsernameNotFoundException, SecurityException {
        User userToUpdate = loadUserById(userId);

        if (!userToUpdate.getEmail().equals(authenticatedUserEmail)) {
            throw new SecurityException("User is not authorized to update this profile.");
        }

        if (request.getName() != null) {
            userToUpdate.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            userToUpdate.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBio() != null) {
            userToUpdate.setBio(request.getBio());
        }
        if (request.getProfilePictureUrl() != null) {
            userToUpdate.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        return userRepository.save(userToUpdate);
    }
}