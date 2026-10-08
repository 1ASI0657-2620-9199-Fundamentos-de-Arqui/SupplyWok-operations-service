package aurora.supply_wok.platform.iot.domain.model.queries;

public record GetSensorByIdQuery(Long restaurantId, Long sensorId) {
    public GetSensorByIdQuery(Long sensorId) {
        this(null, sensorId);
    }
}
