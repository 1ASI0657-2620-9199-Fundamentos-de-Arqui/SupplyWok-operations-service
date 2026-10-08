package aurora.supply_wok.platform.iot.interfaces.acl;

import aurora.supply_wok.platform.iot.application.commandservices.SensorCommandService;
import aurora.supply_wok.platform.iot.application.queryservices.SensorQueryService;
import aurora.supply_wok.platform.iot.domain.model.commands.CreateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.queries.GetSensorByIdQuery;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ACL facade that exposes IoT bounded context capabilities to other bounded contexts.
 *
 * <p>Provides a simplified, decoupled integration surface for creating and querying sensors
 * without leaking IoT internal domain models or entities.</p>
 */
@Service
public class IotContextFacade {

    private final SensorCommandService sensorCommandService;
    private final SensorQueryService sensorQueryService;

    public IotContextFacade(SensorCommandService sensorCommandService, SensorQueryService sensorQueryService) {
        this.sensorCommandService = sensorCommandService;
        this.sensorQueryService = sensorQueryService;
    }

    public Long createSensor(String name, double minValue, double maxValue, boolean enabled, double lastValue, String typeName) {
        try {
            var sensorType = ESensorType.valueOf(typeName);
            var createSensorCommand = new CreateSensorCommand(name, minValue, maxValue, enabled, lastValue, sensorType);
            return sensorCommandService.handle(createSensorCommand);
        } catch (Exception e) {
            return 0L;
        }
    }

    public String fetchSensorNameById(Long sensorId) {
        var query = new GetSensorByIdQuery(sensorId);
        var sensor = sensorQueryService.handle(query);
        return sensor.map(s -> s.getName()).orElse("");
    }

    public boolean isSensorEnabled(Long sensorId) {
        var query = new GetSensorByIdQuery(sensorId);
        var sensor = sensorQueryService.handle(query);
        return sensor.map(s -> s.isEnabled()).orElse(false);
    }

    public Optional<Double> fetchSensorLastValueById(Long sensorId) {
        var query = new GetSensorByIdQuery(sensorId);
        return sensorQueryService.handle(query).map(sensor -> sensor.getLastValue());
    }
}
