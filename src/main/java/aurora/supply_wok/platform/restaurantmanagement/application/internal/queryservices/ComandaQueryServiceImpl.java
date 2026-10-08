package aurora.supply_wok.platform.restaurantmanagement.application.internal.queryservices;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetAllComandasQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandaByIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandasByTableIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.ComandaQueryService;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.ComandaRepository;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.RestaurantTableRepository;
import aurora.supply_wok.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComandaQueryServiceImpl implements ComandaQueryService {

    private final ComandaRepository comandaRepository;
    private final RestaurantTableRepository tableRepository;

    public ComandaQueryServiceImpl(ComandaRepository comandaRepository, RestaurantTableRepository tableRepository) {
        this.comandaRepository = comandaRepository;
        this.tableRepository = tableRepository;
    }

    @Override
    public List<Comanda> handle(GetAllComandasQuery query) {
        if (query.restaurantId() != null) {
            return comandaRepository.findAllByRestaurantId(query.restaurantId());
        }
        return comandaRepository.findAll();
    }

    @Override
    public Optional<Comanda> handle(GetComandaByIdQuery query) {
        if (query.restaurantId() != null) {
            return comandaRepository.findByIdAndRestaurantId(query.comandaId(), query.restaurantId());
        }
        return comandaRepository.findById(query.comandaId());
    }

    @Override
    public List<Comanda> handle(GetComandasByTableIdQuery query) {
        if (query.restaurantId() != null) {
            var table = tableRepository.findByIdAndRestaurantId(query.tableId(), query.restaurantId());
            if (table.isEmpty()) {
                throw new ResourceNotFoundException("Table with id %d not found".formatted(query.tableId()));
            }
            return comandaRepository.findByTableIdAndRestaurantId(query.tableId(), query.restaurantId());
        }
        return comandaRepository.findByTableId(query.tableId());
    }
}
