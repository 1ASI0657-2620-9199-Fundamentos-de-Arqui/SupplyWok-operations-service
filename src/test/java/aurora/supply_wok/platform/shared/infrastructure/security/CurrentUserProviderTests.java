package aurora.supply_wok.platform.shared.infrastructure.security;

import aurora.supply_wok.platform.shared.domain.exceptions.UnauthorizedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrentUserProviderTests {

    private CurrentUserProvider currentUserProvider;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void get_whenUserIsAuthenticated_returnsAuthenticatedUser() {
        // Arrange
        var user = new AuthenticatedUser(42L, "user@supplywok.com", List.of("RESTAURANT"));
        var auth = new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        var result = currentUserProvider.get();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(42L);
        assertThat(result.email()).isEqualTo("user@supplywok.com");
    }

    @Test
    void get_whenAuthenticationIsMissing_throwsUnauthorizedException() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> currentUserProvider.get());
    }

    @Test
    void userId_whenUserHasValidId_returnsUserId() {
        // Arrange
        var user = new AuthenticatedUser(77L, "tenant@supplywok.com", List.of("RESTAURANT"));
        var auth = new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Long result = currentUserProvider.userId();

        // Assert
        assertThat(result).isEqualTo(77L);
    }

    @Test
    void userId_whenUserHasNoId_throwsUnauthorizedException() {
        // Arrange
        var user = new AuthenticatedUser(null, "tenant@supplywok.com", List.of("RESTAURANT"));
        var auth = new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> currentUserProvider.userId());
    }

    @Test
    void email_whenUserHasValidEmail_returnsEmail() {
        // Arrange
        var user = new AuthenticatedUser(77L, "supplier@supplywok.com", List.of("SUPPLIER"));
        var auth = new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        String result = currentUserProvider.email();

        // Assert
        assertThat(result).isEqualTo("supplier@supplywok.com");
    }
}
