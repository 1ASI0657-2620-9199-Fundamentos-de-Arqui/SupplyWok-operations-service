package aurora.supply_wok.platform.iot.domain.model.commands;

import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;

public record CreateSensorCommand(
        Long restaurantId,
        String name,
        double minValue,
        double maxValue,
        boolean enabled,
        double lastValue,
        ESensorType type
) {
    public CreateSensorCommand(String name, double minValue, double maxValue, boolean enabled, double lastValue, ESensorType type) {
        this(1L, name, minValue, maxValue, enabled, lastValue, type);
    }
}
