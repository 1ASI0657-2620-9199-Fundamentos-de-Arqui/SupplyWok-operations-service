package aurora.supply_wok.platform.iot.domain.model.events;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;

public record SensorThresholdBreachedEvent(
        Long sensorId,
        String sensorName,
        String sensorType,
        double minValue,
        double maxValue,
        double recordedValue
) {
    public static SensorThresholdBreachedEvent from(Sensor sensor) {
        return new SensorThresholdBreachedEvent(
                sensor.getId(),
                sensor.getName(),
                sensor.getType().name(),
                sensor.getMinValue(),
                sensor.getMaxValue(),
                sensor.getLastValue()
        );
    }
}
