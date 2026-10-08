package aurora.supply_wok.platform.iot.domain.model.commands;

public record DeleteSensorCommand(Long restaurantId, Long sensorId) {
    public DeleteSensorCommand(Long sensorId) {
        this(null, sensorId);
    }
}
