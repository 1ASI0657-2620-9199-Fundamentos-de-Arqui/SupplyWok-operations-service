package aurora.supply_wok.platform.shared.domain.exceptions;

/**
 * Thrown when an unauthenticated caller attempts to access protected tenant context.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
