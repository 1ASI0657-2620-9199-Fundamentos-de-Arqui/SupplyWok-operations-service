package aurora.supply_wok.platform.iot.interfaces.rest.transform;

import aurora.supply_wok.platform.iot.domain.model.commands.UpdateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import aurora.supply_wok.platform.iot.interfaces.rest.resources.UpdateSensorResource;

/**
 * Assembler to convert a {@link UpdateSensorResource} to an {@link UpdateSensorCommand}.
 */
public class UpdateSensorCommandFromResourceAssembler {

    /**
     * Converts an UpdateSensorResource and a sensor ID to an UpdateSensorCommand.
     *
     * @param sensorId the ID of the sensor to update
     * @param resource the {@link UpdateSensorResource} to convert
     * @return the corresponding {@link UpdateSensorCommand}
     * @throws IllegalArgumentException if the sensor type is invalid
     */
    public static UpdateSensorCommand toCommandFromResource(Long sensorId, UpdateSensorResource resource) {
        ESensorType sensorType;
        try {
            sensorType = ESensorType.valueOf(resource.type());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid sensor type: " + resource.type());
        }

        return new UpdateSensorCommand(
                sensorId,
                resource.name(),
                resource.minValue(),
                resource.maxValue(),
                resource.enabled(),
                resource.lastValue() != null ? resource.lastValue() : 0.0,
                sensorType
        );
    }
}
