package aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {
    List<Comanda> findByTableId(Long tableId);
}
