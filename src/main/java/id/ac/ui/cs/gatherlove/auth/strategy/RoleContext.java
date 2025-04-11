package id.ac.ui.cs.gatherlove.auth.strategy;

import id.ac.ui.cs.gatherlove.auth.model.Role;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleContext {
    private final Map<Role, RoleStrategy> strategies;

    public RoleContext(List<RoleStrategy> strategyList) {
        this.strategies = new HashMap<>();
        this.strategies.put(Role.ADMIN, new AdminStrategy());
        this.strategies.put(Role.FUNDRAISER, new FundraiserStrategy());
    }

    public RoleStrategy getStrategy(Role role) {
        return strategies.get(role);
    }
}