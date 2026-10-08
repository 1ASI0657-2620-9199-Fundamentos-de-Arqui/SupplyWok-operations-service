package aurora.supply_wok.platform.restaurantmanagement.domain.model.services;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetAllComandasQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandaByIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandasByTableIdQuery;

import java.util.List;
import java.util.Optional;

public interface ComandaQueryService {
    List<Comanda> handle(GetAllComandasQuery query);
    Optional<Comanda> handle(GetComandaByIdQuery query);
    List<Comanda> handle(GetComandasByTableIdQuery query);
}
