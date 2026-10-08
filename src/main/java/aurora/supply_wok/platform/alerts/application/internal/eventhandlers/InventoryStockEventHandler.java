package aurora.supply_wok.platform.alerts.application.internal.eventhandlers;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.inventory.interfaces.events.SupplyMinimumStockReachedIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Turns low-stock inventory events into restaurant alerts.
 */
@Service
@Slf4j
public class InventoryStockEventHandler {

    private final AlertCommandService alertCommandService;

    public InventoryStockEventHandler(AlertCommandService alertCommandService) {
        this.alertCommandService = alertCommandService;
    }

    @EventListener
    public void on(SupplyMinimumStockReachedIntegrationEvent event) {
        try {
            var detail = "Supply %s reached minimum stock: %d/%d."
                    .formatted(event.name(), event.currentStock(), event.minimumStockLevel());
            alertCommandService.handle(new CreateRestaurantAlertCommand(EAlertSeverity.HIGH, detail, null));
        } catch (Exception ex) {
            log.error("Could not create low-stock alert for supply {}", event.supplyId(), ex);
        }
    }
}
