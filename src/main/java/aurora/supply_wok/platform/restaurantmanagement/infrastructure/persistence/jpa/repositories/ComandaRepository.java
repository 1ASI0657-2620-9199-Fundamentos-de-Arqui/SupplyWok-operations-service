package aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {
    List<Comanda> findByTableId(Long tableId);
    Optional<Comanda> findByIdAndRestaurantId(Long id, Long restaurantId);
    List<Comanda> findAllByRestaurantId(Long restaurantId);
    List<Comanda> findByTableIdAndRestaurantId(Long tableId, Long restaurantId);
    boolean existsByIdAndRestaurantId(Long id, Long restaurantId);
}
