package id.ac.ui.cs.gatherlove.auth.strategy;

import id.ac.ui.cs.gatherlove.auth.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RoleContextIntegrationTest {

    @Autowired
    private RoleContext roleContext;

    @Test
    void contextLoads() {
        assertNotNull(roleContext);
    }

    @Test
    void adminStrategyBeanExists() {
        RoleStrategy strategy = roleContext.getStrategy(Role.ADMIN);
        assertNotNull(strategy);
        assertFalse(strategy.canCreateCampaign());
    }
}