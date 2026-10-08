package aurora.supply_wok.platform.iot.domain.model.commands;

import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;

/**
 * Command to update an existing sensor.
 *
 * @param sensorId  the ID of the sensor to update
 * @param name      the new name of the sensor
 * @param minValue  the new minimum value
 * @param maxValue  the new maximum value
 * @param enabled   the new enabled status
 * @param lastValue the new last recorded value
 * @param type      the new sensor type
 */
public record UpdateSensorCommand(Long sensorId, String name, double minValue, double maxValue, boolean enabled, double lastValue, ESensorType type) {
}
