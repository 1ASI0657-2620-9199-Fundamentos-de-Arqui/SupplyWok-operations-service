package aurora.supply_wok.platform.iot.application.internal.eventhandlers;

import aurora.supply_wok.platform.iot.domain.model.events.SensorThresholdBreachedEvent;
import aurora.supply_wok.platform.iot.interfaces.events.SensorThresholdBreachedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Publishes sensor threshold breaches as integration events.
 */
@Service
public class SensorThresholdBreachedEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    public SensorThresholdBreachedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void on(SensorThresholdBreachedEvent event) {
        eventPublisher.publishEvent(new SensorThresholdBreachedIntegrationEvent(
                event.sensorId(),
                event.sensorName(),
                event.sensorType(),
                event.minValue(),
                event.maxValue(),
                event.recordedValue()
        ));
    }
}
