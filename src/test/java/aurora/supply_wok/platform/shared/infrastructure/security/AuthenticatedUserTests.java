package aurora.supply_wok.platform.shared.infrastructure.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticatedUserTests {

    @Test
    void hasRole_whenRoleMatchesWithoutPrefix_returnsTrue() {
        // Arrange
        var user = new AuthenticatedUser(10L, "owner@restaurant.com", List.of("RESTAURANT"));

        // Act
        boolean result = user.hasRole("RESTAURANT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void hasRole_whenRoleHasRolePrefix_returnsTrue() {
        // Arrange
        var user = new AuthenticatedUser(10L, "owner@restaurant.com", List.of("ROLE_RESTAURANT"));

        // Act
        boolean result = user.hasRole("RESTAURANT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void hasRole_whenQueryHasRolePrefix_returnsTrue() {
        // Arrange
        var user = new AuthenticatedUser(10L, "owner@restaurant.com", List.of("RESTAURANT"));

        // Act
        boolean result = user.hasRole("ROLE_RESTAURANT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void hasRole_whenRoleDoesNotMatch_returnsFalse() {
        // Arrange
        var user = new AuthenticatedUser(10L, "supplier@example.com", List.of("SUPPLIER"));

        // Act
        boolean result = user.hasRole("RESTAURANT");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void hasRole_whenRolesListIsNull_returnsFalse() {
        // Arrange
        var user = new AuthenticatedUser(10L, "supplier@example.com", null);

        // Act
        boolean result = user.hasRole("RESTAURANT");

        // Assert
        assertThat(result).isFalse();
    }
}
