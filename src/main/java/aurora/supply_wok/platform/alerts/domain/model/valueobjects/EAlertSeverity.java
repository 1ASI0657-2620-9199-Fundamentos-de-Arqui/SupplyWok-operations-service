package aurora.supply_wok.platform.alerts.domain.model.valueobjects;

public enum EAlertSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static EAlertSeverity fromStoredValue(String value) {
        if (value == null || value.isBlank()) {
            return LOW;
        }

        for (var severity : values()) {
            if (severity.name().equalsIgnoreCase(value.trim())) {
                return severity;
            }
        }

        throw new IllegalArgumentException("Unsupported alert severity: " + value);
    }

    public String toStoredValue() {
        var normalized = name().toLowerCase();
        return normalized.substring(0, 1).toUpperCase() + normalized.substring(1);
    }
}
