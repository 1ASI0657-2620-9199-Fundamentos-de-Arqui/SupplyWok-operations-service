package aurora.supply_wok.platform.restaurantmanagement.application.internal.queryservices;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetAllComandasQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandaByIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandasByTableIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.ComandaQueryService;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.ComandaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComandaQueryServiceImpl implements ComandaQueryService {

    private final ComandaRepository comandaRepository;

    public ComandaQueryServiceImpl(ComandaRepository comandaRepository) {
        this.comandaRepository = comandaRepository;
    }

    @Override
    public List<Comanda> handle(GetAllComandasQuery query) {
        return comandaRepository.findAll();
    }

    @Override
    public Optional<Comanda> handle(GetComandaByIdQuery query) {
        return comandaRepository.findById(query.comandaId());
    }

    @Override
    public List<Comanda> handle(GetComandasByTableIdQuery query) {
        return comandaRepository.findByTableId(query.tableId());
    }
}
