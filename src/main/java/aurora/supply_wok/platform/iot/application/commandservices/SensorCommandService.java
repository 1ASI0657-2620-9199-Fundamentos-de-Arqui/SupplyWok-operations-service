package aurora.supply_wok.platform.iot.application.commandservices;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import aurora.supply_wok.platform.iot.domain.model.commands.CreateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.commands.DeleteSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.commands.UpdateSensorCommand;

import java.util.Optional;

/**
 * Service interface for handling sensor-related commands.
 */
public interface SensorCommandService {
    Long handle(CreateSensorCommand command);
    Optional<Sensor> handle(UpdateSensorCommand command);
    void handle(DeleteSensorCommand command);
}
