package aurora.supply_wok.platform.iot.domain.model.queries;

/**
 * Query to get a sensor by its ID.
 *
 * @param sensorId the ID of the sensor to retrieve
 */
public record GetSensorByIdQuery(Long sensorId) {
}
