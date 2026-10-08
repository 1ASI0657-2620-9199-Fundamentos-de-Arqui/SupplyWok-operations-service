package aurora.supply_wok.platform.alerts.domain.model.queries;

public record GetAllRestaurantAlertsQuery(Long restaurantId) {
    public GetAllRestaurantAlertsQuery() {
        this(null);
    }
}
