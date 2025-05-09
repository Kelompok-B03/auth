package id.ac.ui.cs.gatherlove.auth.repository;

import id.ac.ui.cs.gatherlove.auth.model.Role;
import id.ac.ui.cs.gatherlove.auth.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

//    @Test
//    @DisplayName("Should save and find user by email")
//    void testFindByEmail() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("password123");
//        user.setRoles(new HashSet<>());
//        user.setActive(true);
//        user.setName("Test User");
//
//        User savedUser = userRepository.save(user);
//        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
//
//        assertThat(foundUser).isPresent();
//        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
//        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
//    }

//    @Test
//    @DisplayName("Should save and find user by UUID")
//    void testFindById() {
//        User user = new User();
//        user.setEmail("another@example.com");
//        user.setPassword("password456");
//        user.setRoles(Set.of(new Role(null, "ROLE_USER")));
//        user.setActive(true);
//        user.setName("Another User");
//
//        User savedUser = userRepository.save(user);
//        UUID id = savedUser.getId();
//
//        Optional<User> foundUser = userRepository.findById(id);
//
//        assertThat(foundUser).isPresent();
//        assertThat(foundUser.get().getId()).isEqualTo(id);
//    }
}
