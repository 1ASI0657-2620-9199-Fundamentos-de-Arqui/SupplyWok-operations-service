package aurora.supply_wok.platform.iot.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorPersistenceRepository extends JpaRepository<Sensor, Long> {
}
