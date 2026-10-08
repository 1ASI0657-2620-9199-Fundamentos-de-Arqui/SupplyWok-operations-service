package aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    Optional<RestaurantTable> findByIdAndRestaurantId(Long id, Long restaurantId);
    List<RestaurantTable> findAllByRestaurantId(Long restaurantId);
    boolean existsByIdAndRestaurantId(Long id, Long restaurantId);
    boolean existsByRestaurantIdAndNumber(Long restaurantId, int number);
}
