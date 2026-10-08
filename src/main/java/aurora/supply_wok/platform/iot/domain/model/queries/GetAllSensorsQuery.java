package aurora.supply_wok.platform.iot.domain.model.queries;

public record GetAllSensorsQuery(Long restaurantId) {
    public GetAllSensorsQuery() {
        this(null);
    }
}
