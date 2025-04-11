package id.ac.ui.cs.gatherlove.auth.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminStrategyTest {

    private final AdminStrategy strategy = new AdminStrategy();

    @Test
    void adminCanVerifyCampaign() {
        assertTrue(strategy.canVerifyCampaign());
    }

    @Test
    void adminCannotCreateCampaign() {
        assertFalse(strategy.canCreateCampaign());
    }
}