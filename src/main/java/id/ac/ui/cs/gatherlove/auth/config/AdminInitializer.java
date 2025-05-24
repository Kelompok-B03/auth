package id.ac.ui.cs.gatherlove.auth.config;

import id.ac.ui.cs.gatherlove.auth.model.Role;
import id.ac.ui.cs.gatherlove.auth.model.User;
import id.ac.ui.cs.gatherlove.auth.repository.RoleRepository;
import id.ac.ui.cs.gatherlove.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.NoSuchElementException;

@Component
@Order(2)
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.email:assistant@gatherlove.com}")
    private String adminEmail;

    @Value("${admin.default.password:ADPROB03SUKSES!}")
    private String adminPassword;

    @Value("${admin.default.name:Default Admin}")
    private String adminName;


    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new NoSuchElementException(
                        "Role 'ADMIN' not found in the database. " +
                                "Please ensure it is created manually or via a database migration script."
                ));

        if (!userRepository.existsByRolesContains(adminRole)) {
            User adminUser = User.builder(adminEmail, passwordEncoder.encode(adminPassword))
                    .name(adminName)
                    .roles(Collections.singleton(adminRole))
                    .isActive(true)
                    .build();
            userRepository.save(adminUser);
            System.out.println(">>> Default admin user created: " + adminEmail);
            System.out.println(">>> PLEASE CHANGE THE DEFAULT ADMIN PASSWORD IMMEDIATELY!");
        } else {
            System.out.println(">>> Admin user already exists. No default admin created.");
        }
    }
}