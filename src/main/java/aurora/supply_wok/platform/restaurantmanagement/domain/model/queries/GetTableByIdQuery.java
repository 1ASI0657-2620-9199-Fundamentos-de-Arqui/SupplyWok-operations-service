package aurora.supply_wok.platform.restaurantmanagement.domain.model.queries;

public record GetTableByIdQuery(Long restaurantId, Long tableId) {
    public GetTableByIdQuery(Long tableId) {
        this(null, tableId);
    }
}
