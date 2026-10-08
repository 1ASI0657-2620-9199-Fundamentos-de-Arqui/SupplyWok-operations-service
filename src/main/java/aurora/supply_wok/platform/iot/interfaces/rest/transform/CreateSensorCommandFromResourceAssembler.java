package aurora.supply_wok.platform.iot.interfaces.rest.transform;

import aurora.supply_wok.platform.iot.domain.model.commands.CreateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import aurora.supply_wok.platform.iot.interfaces.rest.resources.CreateSensorResource;

/**
 * Assembler to convert a {@link CreateSensorResource} to a {@link CreateSensorCommand}.
 */
public class CreateSensorCommandFromResourceAssembler {

    /**
     * Converts a CreateSensorResource to a CreateSensorCommand.
     *
     * @param resource the {@link CreateSensorResource} to convert
     * @return the corresponding {@link CreateSensorCommand}
     * @throws IllegalArgumentException if the sensor type is invalid
     */
    public static CreateSensorCommand toCommandFromResource(CreateSensorResource resource) {
        ESensorType sensorType;
        try {
            sensorType = ESensorType.valueOf(resource.type());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid sensor type: " + resource.type());
        }

        return new CreateSensorCommand(
                resource.name(),
                resource.minValue(),
                resource.maxValue(),
                resource.enabled(),
                resource.lastValue() != null ? resource.lastValue() : 0.0,
                sensorType
        );
    }
}
