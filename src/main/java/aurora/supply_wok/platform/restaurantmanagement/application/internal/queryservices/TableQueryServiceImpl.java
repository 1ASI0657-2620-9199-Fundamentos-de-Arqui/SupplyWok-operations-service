package aurora.supply_wok.platform.restaurantmanagement.application.internal.queryservices;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetAllTablesQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetTableByIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.TableQueryService;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.RestaurantTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TableQueryServiceImpl implements TableQueryService {

    private final RestaurantTableRepository tableRepository;

    public TableQueryServiceImpl(RestaurantTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public List<RestaurantTable> handle(GetAllTablesQuery query) {
        return tableRepository.findAll();
    }

    @Override
    public Optional<RestaurantTable> handle(GetTableByIdQuery query) {
        return tableRepository.findById(query.tableId());
    }
}
