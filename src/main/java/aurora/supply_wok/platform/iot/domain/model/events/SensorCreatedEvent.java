package aurora.supply_wok.platform.iot.domain.model.events;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;

public record SensorCreatedEvent(
        Long sensorId,
        String name,
        String type,
        double lastValue,
        boolean enabled
) {
    public static SensorCreatedEvent from(Sensor sensor) {
        return new SensorCreatedEvent(
                sensor.getId(),
                sensor.getName(),
                sensor.getType().name(),
                sensor.getLastValue(),
                sensor.isEnabled()
        );
    }
}
