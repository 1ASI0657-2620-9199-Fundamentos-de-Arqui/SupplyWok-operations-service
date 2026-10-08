package aurora.supply_wok.platform.alerts.application.internal.eventhandlers;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateSupplierAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderCreatedIntegrationEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderStatusChangedIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Turns purchase order lifecycle events into supplier and restaurant alerts.
 */
@Service
@Slf4j
public class PurchaseOrderEventHandler {

    private final AlertCommandService alertCommandService;

    public PurchaseOrderEventHandler(AlertCommandService alertCommandService) {
        this.alertCommandService = alertCommandService;
    }

    @EventListener
    public void on(PurchaseOrderCreatedIntegrationEvent event) {
        try {
            var severity = "High".equalsIgnoreCase(event.priority()) ? EAlertSeverity.HIGH : EAlertSeverity.MEDIUM;
            var detail = "New purchase order %s from %s.".formatted(event.code(), event.restaurantName());
            alertCommandService.handle(new CreateSupplierAlertCommand(severity, detail));
        } catch (Exception ex) {
            log.warn("Could not create supplier alert for purchase order {}: {}", event.purchaseOrderId(), ex.getMessage());
        }
    }

    @EventListener
    public void on(PurchaseOrderStatusChangedIntegrationEvent event) {
        try {
            if ("Delayed".equals(event.currentStatus())) {
                var detail = "Purchase order %s from supplier %s is delayed.".formatted(event.code(), event.supplierName());
                alertCommandService.handle(new CreateRestaurantAlertCommand(EAlertSeverity.HIGH, detail, null));
                return;
            }

            if ("Delivered".equals(event.currentStatus())) {
                var detail = "Purchase order %s from supplier %s was delivered.".formatted(event.code(), event.supplierName());
                alertCommandService.handle(new CreateRestaurantAlertCommand(EAlertSeverity.LOW, detail, null));
            }
        } catch (Exception ex) {
            log.warn("Could not create restaurant alert for purchase order {}: {}", event.purchaseOrderId(), ex.getMessage());
        }
    }
}
