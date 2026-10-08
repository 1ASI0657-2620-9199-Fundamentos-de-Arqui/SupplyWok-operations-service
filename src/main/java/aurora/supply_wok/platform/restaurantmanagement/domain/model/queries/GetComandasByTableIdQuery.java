package aurora.supply_wok.platform.restaurantmanagement.domain.model.queries;

public record GetComandasByTableIdQuery(Long restaurantId, Long tableId) {
    public GetComandasByTableIdQuery(Long tableId) {
        this(null, tableId);
    }
}
