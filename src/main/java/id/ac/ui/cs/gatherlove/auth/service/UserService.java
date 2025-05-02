package id.ac.ui.cs.gatherlove.auth.service;

import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
