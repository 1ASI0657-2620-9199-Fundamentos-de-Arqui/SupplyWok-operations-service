package aurora.supply_wok.platform.alerts.domain.model.valueobjects;

public enum EAlertStatus {
    PENDING,
    ACKNOWLEDGED;

    public static EAlertStatus fromStoredValue(String value) {
        if (value == null || value.isBlank()) {
            return PENDING;
        }

        var normalizedValue = value.trim();
        if ("OPEN".equalsIgnoreCase(normalizedValue)) {
            return PENDING;
        }
        if ("RESOLVED".equalsIgnoreCase(normalizedValue)) {
            return ACKNOWLEDGED;
        }

        for (var status : values()) {
            if (status.name().equalsIgnoreCase(normalizedValue)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unsupported alert status: " + value);
    }

    public String toStoredValue() {
        return switch (this) {
            case PENDING -> "Pending";
            case ACKNOWLEDGED -> "Acknowledged";
        };
    }
}
