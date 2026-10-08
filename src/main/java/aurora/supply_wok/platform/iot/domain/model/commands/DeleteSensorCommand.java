package aurora.supply_wok.platform.iot.domain.model.commands;

/**
 * Command to delete a sensor.
 *
 * @param sensorId the ID of the sensor to delete
 */
public record DeleteSensorCommand(Long sensorId) {
}
