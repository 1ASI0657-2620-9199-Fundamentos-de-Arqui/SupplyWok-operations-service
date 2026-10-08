package aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertPersistenceRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM SupplierAlert a")
    List<SupplierAlert> findAllSupplierAlerts();

    @Query("SELECT a FROM SupplierAlert a WHERE LOWER(a.supplierEmail) = LOWER(:supplierEmail)")
    List<SupplierAlert> findAllSupplierAlertsBySupplierEmail(@Param("supplierEmail") String supplierEmail);

    @Query("SELECT a FROM SupplierAlert a WHERE a.id = :id AND LOWER(a.supplierEmail) = LOWER(:supplierEmail)")
    Optional<SupplierAlert> findSupplierAlertByIdAndSupplierEmail(@Param("id") Long id, @Param("supplierEmail") String supplierEmail);

    @Query("SELECT a FROM RestaurantAlert a")
    List<RestaurantAlert> findAllRestaurantAlerts();

    @Query("SELECT a FROM RestaurantAlert a WHERE a.restaurantId = :restaurantId")
    List<RestaurantAlert> findAllRestaurantAlertsByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT a FROM RestaurantAlert a WHERE a.id = :id AND a.restaurantId = :restaurantId")
    Optional<RestaurantAlert> findRestaurantAlertByIdAndRestaurantId(@Param("id") Long id, @Param("restaurantId") Long restaurantId);
}
