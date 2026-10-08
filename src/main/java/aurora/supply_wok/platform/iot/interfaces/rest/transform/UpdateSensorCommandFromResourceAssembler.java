package aurora.supply_wok.platform.iot.interfaces.rest.transform;

import aurora.supply_wok.platform.iot.domain.model.commands.UpdateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import aurora.supply_wok.platform.iot.interfaces.rest.resources.UpdateSensorResource;

/**
 * Assembler to convert a {@link UpdateSensorResource} to an {@link UpdateSensorCommand}.
 */
public class UpdateSensorCommandFromResourceAssembler {

    public static UpdateSensorCommand toCommandFromResource(Long sensorId, UpdateSensorResource resource) {
        return toCommandFromResource(null, sensorId, resource);
    }

    public static UpdateSensorCommand toCommandFromResource(Long restaurantId, Long sensorId, UpdateSensorResource resource) {
        ESensorType sensorType;
        try {
            sensorType = ESensorType.valueOf(resource.type());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid sensor type: " + resource.type());
        }

        return new UpdateSensorCommand(
                restaurantId,
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
