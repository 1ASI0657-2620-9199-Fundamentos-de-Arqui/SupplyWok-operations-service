package aurora.supply_wok.platform.iot.interfaces.events;

/**
 * Published language event raised when a sensor value leaves its configured safe range.
 */
public record SensorThresholdBreachedIntegrationEvent(
        Long sensorId,
        String sensorName,
        String sensorType,
        double minValue,
        double maxValue,
        double recordedValue
) {
}
