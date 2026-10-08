package aurora.supply_wok.platform.iot.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorPersistenceRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findByIdAndRestaurantId(Long id, Long restaurantId);
    List<Sensor> findAllByRestaurantId(Long restaurantId);
    boolean existsByIdAndRestaurantId(Long id, Long restaurantId);
}
