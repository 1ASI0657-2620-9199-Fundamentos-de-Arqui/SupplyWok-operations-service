package aurora.supply_wok.platform.alerts.application.internal.commandservices;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.commands.AcknowledgeAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateAlertRestaurantFromInventoryCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateSupplierAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.alerts.infrastructure.clients.InventoryServiceClient;
import aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import aurora.supply_wok.platform.iot.interfaces.acl.IotContextFacade;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlertCommandServiceImpl implements AlertCommandService {

    private final AlertPersistenceRepository alertPersistenceRepository;
    private final IotContextFacade iotContextFacade;
    private final InventoryServiceClient inventoryServiceClient;
    private final DomainEventPublisher domainEventPublisher;

    public AlertCommandServiceImpl(AlertPersistenceRepository alertPersistenceRepository,
                                   IotContextFacade iotContextFacade,
                                   InventoryServiceClient inventoryServiceClient,
                                   DomainEventPublisher domainEventPublisher) {
        this.alertPersistenceRepository = alertPersistenceRepository;
        this.iotContextFacade = iotContextFacade;
        this.inventoryServiceClient = inventoryServiceClient;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Long handle(CreateSupplierAlertCommand command) {
        var supplierAlert = new SupplierAlert(command);
        try {
            alertPersistenceRepository.save(supplierAlert);
            supplierAlert.onCreated();
            domainEventPublisher.publishAndClear(supplierAlert);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while saving supplier alert: " + e.getMessage());
        }
        return supplierAlert.getId();
    }

    @Override
    public Long handle(CreateRestaurantAlertCommand command) {
        var sensorName = command.sensorId() == null ? "System" : iotContextFacade.fetchSensorNameById(command.sensorId());
        if (command.sensorId() != null && sensorName.isBlank()) {
            throw new IllegalArgumentException("Sensor with ID " + command.sensorId() + " does not exist.");
        }

        var sensorId = command.sensorId() == null ? 0L : command.sensorId();
        var restaurantAlert = new RestaurantAlert(command.severity(), command.detail(), sensorId, sensorName);
        try {
            alertPersistenceRepository.save(restaurantAlert);
            restaurantAlert.onCreated();
            domainEventPublisher.publishAndClear(restaurantAlert);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while saving restaurant alert: " + e.getMessage());
        }
        return restaurantAlert.getId();
    }

    @Override
    public Optional<Alert> handle(CreateAlertRestaurantFromInventoryCommand command) {
        var sensorLastValue = iotContextFacade.fetchSensorLastValueById(command.sensorId())
                .orElseThrow(() -> new IllegalArgumentException("Sensor with ID " + command.sensorId() + " does not exist."));
        var sensorName = iotContextFacade.fetchSensorNameById(command.sensorId());
        var inventoryStock = inventoryServiceClient.getTotalSupplyStock();

        if (Math.abs(inventoryStock - sensorLastValue) < 0.000001d) {
            return Optional.empty();
        }

        var detail = "Inventory stock (" + inventoryStock + ") differs from sensor last value (" + sensorLastValue + ").";
        var restaurantAlert = new RestaurantAlert(EAlertSeverity.MEDIUM, detail, command.sensorId(), sensorName);
        try {
            alertPersistenceRepository.save(restaurantAlert);
            restaurantAlert.onCreated();
            domainEventPublisher.publishAndClear(restaurantAlert);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while saving restaurant alert: " + e.getMessage());
        }
        return Optional.of(restaurantAlert);
    }

    @Override
    public Optional<Alert> handle(AcknowledgeAlertCommand command) {
        var alert = alertPersistenceRepository.findById(command.alertId());
        if (alert.isEmpty()) {
            return Optional.empty();
        }

        alert.get().acknowledge();
        try {
            alertPersistenceRepository.save(alert.get());
            domainEventPublisher.publishAndClear(alert.get());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while acknowledging alert: " + e.getMessage());
        }
        return alert;
    }
}
