package aurora.supply_wok.platform.iot.application.internal.eventhandlers;

import aurora.supply_wok.platform.iot.domain.model.events.SensorThresholdBreachedEvent;
import aurora.supply_wok.platform.iot.interfaces.events.SensorThresholdBreachedIntegrationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SensorThresholdBreachedEventHandlerTests {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SensorThresholdBreachedEventHandler eventHandler;

    @Test
    void on_whenThresholdBreachedEventReceived_publishesIntegrationEvent() {
        // Arrange
        var event = new SensorThresholdBreachedEvent(1L, "Cooler", "Temperature", -5.0, 5.0, 10.0);

        // Act
        eventHandler.on(event);

        // Assert
        verify(eventPublisher).publishEvent(any(SensorThresholdBreachedIntegrationEvent.class));
    }
}
