package aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertPersistenceRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM SupplierAlert a")
    List<SupplierAlert> findAllSupplierAlerts();

    @Query("SELECT a FROM RestaurantAlert a")
    List<RestaurantAlert> findAllRestaurantAlerts();
}

