package aurora.supply_wok.platform.shared.domain.exceptions;

/**
 * Thrown when a requested resource does not exist or does not belong to the current tenant.
 * Extends IllegalArgumentException for backward compatibility with existing command validations.
 */
public class ResourceNotFoundException extends IllegalArgumentException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
