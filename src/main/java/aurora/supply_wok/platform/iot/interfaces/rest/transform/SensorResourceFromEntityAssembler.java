package aurora.supply_wok.platform.iot.interfaces.rest.transform;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import aurora.supply_wok.platform.iot.interfaces.rest.resources.SensorResource;

/**
 * Assembler to convert a {@link Sensor} aggregate entity to a {@link SensorResource}.
 */
public class SensorResourceFromEntityAssembler {

    /**
     * Converts a Sensor entity to a SensorResource.
     *
     * @param entity the {@link Sensor} entity to convert
     * @return the corresponding {@link SensorResource}
     */
    public static SensorResource toResourceFromEntity(Sensor entity) {
        return new SensorResource(
                entity.getId(),
                entity.getName(),
                entity.getMinValue(),
                entity.getMaxValue(),
                entity.isEnabled(),
                entity.getLastValue(),
                entity.getType().name()
        );
    }
}
