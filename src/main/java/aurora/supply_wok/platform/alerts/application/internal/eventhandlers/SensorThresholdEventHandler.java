package aurora.supply_wok.platform.alerts.application.internal.eventhandlers;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.iot.interfaces.events.SensorThresholdBreachedIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Turns sensor threshold breaches into restaurant alerts.
 */
@Service
@Slf4j
public class SensorThresholdEventHandler {

    private final AlertCommandService alertCommandService;

    public SensorThresholdEventHandler(AlertCommandService alertCommandService) {
        this.alertCommandService = alertCommandService;
    }

    @EventListener
    public void on(SensorThresholdBreachedIntegrationEvent event) {
        try {
            var detail = "%s sensor %s recorded %.2f outside safe range %.2f - %.2f."
                    .formatted(event.sensorType(), event.sensorName(), event.recordedValue(), event.minValue(), event.maxValue());
            alertCommandService.handle(new CreateRestaurantAlertCommand(EAlertSeverity.CRITICAL, detail, event.sensorId()));
        } catch (Exception ex) {
            log.warn("Could not create threshold alert for sensor {}: {}", event.sensorId(), ex.getMessage());
        }
    }
}
