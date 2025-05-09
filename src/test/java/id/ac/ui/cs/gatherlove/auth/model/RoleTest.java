package id.ac.ui.cs.gatherlove.auth.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void testRequiredArgsConstructor() {
        Role role = new Role();
        assertThat(role).isNotNull();
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        String name = "ROLE_USER";
        Role role = new Role(id, name);

        assertThat(role.getId()).isEqualTo(id);
        assertThat(role.getName()).isEqualTo(name);
    }

    @Test
    void testSettersAndGetters() {
        Role role = new Role();
        UUID id = UUID.randomUUID();
        String name = "ROLE_ADMIN";

        role.setId(id);
        role.setName(name);

        assertThat(role.getId()).isEqualTo(id);
        assertThat(role.getName()).isEqualTo(name);
    }
}
