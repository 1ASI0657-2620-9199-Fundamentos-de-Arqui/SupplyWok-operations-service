package aurora.supply_wok.platform.shared.infrastructure.security;

import aurora.supply_wok.platform.shared.domain.exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Provides access to the currently authenticated user in the security context.
 */
@Component
public class CurrentUserProvider {

    /**
     * Retrieves the authenticated user principal.
     *
     * @return the AuthenticatedUser
     * @throws UnauthorizedException if authentication is absent or not an AuthenticatedUser
     */
    public AuthenticatedUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser user) {
            return user;
        }

        throw new UnauthorizedException("Authentication principal is not an AuthenticatedUser");
    }

    /**
     * Retrieves the current user's ID (used as restaurant tenant ID).
     *
     * @return the user ID
     */
    public Long userId() {
        Long userId = get().userId();
        if (userId == null) {
            throw new UnauthorizedException("Authenticated user does not have a user ID");
        }
        return userId;
    }

    /**
     * Retrieves the current user's email (used as supplier tenant identifier).
     *
     * @return the user email
     */
    public String email() {
        String email = get().email();
        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("Authenticated user does not have an email");
        }
        return email;
    }
}
