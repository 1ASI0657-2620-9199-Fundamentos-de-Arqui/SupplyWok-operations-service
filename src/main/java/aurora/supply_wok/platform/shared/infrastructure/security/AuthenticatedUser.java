package aurora.supply_wok.platform.shared.infrastructure.security;

import java.util.List;

/**
 * Represents the authenticated user principal extracted from the JWT token.
 */
public record AuthenticatedUser(Long userId, String email, List<String> roles) {

    public boolean hasRole(String r) {
        if (roles == null || r == null) {
            return false;
        }
        String target = r.startsWith("ROLE_") ? r.substring(5) : r;
        return roles.stream().anyMatch(role -> {
            String actual = role.startsWith("ROLE_") ? role.substring(5) : role;
            return actual.equalsIgnoreCase(target);
        });
    }
}
