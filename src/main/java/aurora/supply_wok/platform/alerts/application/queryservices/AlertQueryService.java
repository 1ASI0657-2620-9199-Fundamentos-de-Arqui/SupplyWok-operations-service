package aurora.supply_wok.platform.alerts.application.queryservices;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAlertByIdQuery;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllRestaurantAlertsQuery;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllSupplierAlertsQuery;

import java.util.List;
import java.util.Optional;

public interface AlertQueryService {
    Optional<Alert> handle(GetAlertByIdQuery query);
    List<SupplierAlert> handle(GetAllSupplierAlertsQuery query);
    List<RestaurantAlert> handle(GetAllRestaurantAlertsQuery query);
}
