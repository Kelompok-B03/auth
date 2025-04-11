package id.ac.ui.cs.gatherlove.auth.strategy;

import org.springframework.stereotype.Component;

public interface RoleStrategy {
    boolean canCreateCampaign();
    boolean canVerifyCampaign();
}

