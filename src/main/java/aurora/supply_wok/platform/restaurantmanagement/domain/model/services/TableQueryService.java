package aurora.supply_wok.platform.restaurantmanagement.domain.model.services;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetAllTablesQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetTableByIdQuery;

import java.util.List;
import java.util.Optional;

public interface TableQueryService {
    List<RestaurantTable> handle(GetAllTablesQuery query);
    Optional<RestaurantTable> handle(GetTableByIdQuery query);
}
