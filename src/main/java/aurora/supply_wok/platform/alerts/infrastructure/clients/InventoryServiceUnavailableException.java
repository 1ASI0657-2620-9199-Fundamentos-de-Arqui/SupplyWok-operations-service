package aurora.supply_wok.platform.alerts.infrastructure.clients;

/**
 * Exception thrown when the remote inventory service is unavailable or times out.
 */
public class InventoryServiceUnavailableException extends RuntimeException {

    public InventoryServiceUnavailableException(String message) {
        super(message);
    }

    public InventoryServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
