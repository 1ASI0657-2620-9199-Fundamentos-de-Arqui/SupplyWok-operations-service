package aurora.supply_wok.platform.iot.domain.model.events;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;

public record SensorUpdatedEvent(
        Long sensorId,
        String name,
        String type,
        double lastValue,
        boolean enabled
) {
    public static SensorUpdatedEvent from(Sensor sensor) {
        return new SensorUpdatedEvent(
                sensor.getId(),
                sensor.getName(),
                sensor.getType().name(),
                sensor.getLastValue(),
                sensor.isEnabled()
        );
    }
}
