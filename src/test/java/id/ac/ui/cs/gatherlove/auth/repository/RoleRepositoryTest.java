package id.ac.ui.cs.gatherlove.auth.repository;

import id.ac.ui.cs.gatherlove.auth.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

//    @Test
//    @DisplayName("Should save and find role by name")
//    void testFindByName() {
//        Role role = new Role();
//        role.setName("ROLE_USER");
//
//        Role savedRole = roleRepository.save(role);
//
//        Optional<Role> found = roleRepository.findByName("ROLE_USER");
//        assertThat(found).isPresent();
//        assertThat(found.get().getName()).isEqualTo("ROLE_USER");
//        assertThat(found.get().getId()).isEqualTo(savedRole.getId());
//    }

//    @Test
//    @DisplayName("Should save and find role by UUID")
//    void testFindById() {
//        Role role = new Role();
//        role.setName("ROLE_ADMIN");
//
//        Role savedRole = roleRepository.save(role);
//        UUID id = savedRole.getId();
//
//        Optional<Role> found = roleRepository.findById(id);
//        assertThat(found).isPresent();
//        assertThat(found.get().getId()).isEqualTo(id);
//        assertThat(found.get().getName()).isEqualTo("ROLE_ADMIN");
//    }
}
