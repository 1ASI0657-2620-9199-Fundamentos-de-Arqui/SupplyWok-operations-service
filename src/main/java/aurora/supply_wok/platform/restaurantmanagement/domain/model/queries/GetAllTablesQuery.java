package aurora.supply_wok.platform.restaurantmanagement.domain.model.queries;

public record GetAllTablesQuery(Long restaurantId) {
    public GetAllTablesQuery() {
        this(null);
    }
}
