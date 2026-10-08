package aurora.supply_wok.platform.iot.domain.model.commands;

import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;

public record UpdateSensorCommand(
        Long restaurantId,
        Long sensorId,
        String name,
        double minValue,
        double maxValue,
        boolean enabled,
        double lastValue,
        ESensorType type
) {
    public UpdateSensorCommand(Long sensorId, String name, double minValue, double maxValue, boolean enabled, double lastValue, ESensorType type) {
        this(null, sensorId, name, minValue, maxValue, enabled, lastValue, type);
    }
}
