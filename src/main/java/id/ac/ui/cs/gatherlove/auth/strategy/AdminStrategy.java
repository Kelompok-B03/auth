package id.ac.ui.cs.gatherlove.auth.strategy;

import org.springframework.stereotype.Component;

// Admin Strategy
@Component
public class AdminStrategy implements RoleStrategy {
    @Override
    public boolean canCreateCampaign() { return false; }
    @Override
    public boolean canVerifyCampaign() { return true; }
}
