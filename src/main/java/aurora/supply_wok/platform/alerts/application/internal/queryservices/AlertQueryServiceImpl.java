package aurora.supply_wok.platform.alerts.application.internal.queryservices;

import aurora.supply_wok.platform.alerts.application.queryservices.AlertQueryService;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAlertByIdQuery;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllRestaurantAlertsQuery;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllSupplierAlertsQuery;
import aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertQueryServiceImpl implements AlertQueryService {

    private final AlertPersistenceRepository alertPersistenceRepository;

    public AlertQueryServiceImpl(AlertPersistenceRepository alertPersistenceRepository) {
        this.alertPersistenceRepository = alertPersistenceRepository;
    }

    @Override
    public Optional<Alert> handle(GetAlertByIdQuery query) {
        return alertPersistenceRepository.findById(query.alertId());
    }

    @Override
    public List<SupplierAlert> handle(GetAllSupplierAlertsQuery query) {
        return alertPersistenceRepository.findAllSupplierAlerts();
    }

    @Override
    public List<RestaurantAlert> handle(GetAllRestaurantAlertsQuery query) {
        return alertPersistenceRepository.findAllRestaurantAlerts();
    }
}
