package aurora.supply_wok.platform.restaurantmanagement.domain.model.queries;

public record GetComandaByIdQuery(Long restaurantId, Long comandaId) {
    public GetComandaByIdQuery(Long comandaId) {
        this(null, comandaId);
    }
}
