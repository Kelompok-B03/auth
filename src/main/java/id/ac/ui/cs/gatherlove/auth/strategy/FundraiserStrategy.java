package id.ac.ui.cs.gatherlove.auth.strategy;

import org.springframework.stereotype.Component;

// Fundraiser Strategy
@Component
public class FundraiserStrategy implements RoleStrategy {
    @Override
    public boolean canCreateCampaign() { return true; }
    @Override
    public boolean canVerifyCampaign() { return false; }
}
