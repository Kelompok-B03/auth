package id.ac.ui.cs.gatherlove.auth.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FundraiserStrategyTest {

    private final FundraiserStrategy strategy = new FundraiserStrategy();

    @Test
    void fundraiserCanCreateCampaign() {
        assertTrue(strategy.canCreateCampaign());
    }

    @Test
    void fundraiserCannotVerifyCampaign() {
        assertFalse(strategy.canVerifyCampaign());
    }
}
