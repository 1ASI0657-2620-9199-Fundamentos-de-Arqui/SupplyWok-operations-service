package aurora.supply_wok.platform.shared.infrastructure.messaging;

import aurora.supply_wok.platform.inventory.interfaces.events.SupplyMinimumStockReachedIntegrationEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderCreatedIntegrationEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderStatusChangedIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ consumer for integration events received by operations-service.
 * Re-publishes events as local application events so that in-process listeners handle them.
 */
@Component
@Slf4j
@RabbitListener(queues = MessagingConfiguration.QUEUE_NAME)
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class IntegrationEventRabbitListener {

    private final ApplicationEventPublisher applicationEventPublisher;

    public IntegrationEventRabbitListener(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @RabbitHandler
    public void handle(SupplyMinimumStockReachedIntegrationEvent event) {
        log.info("Received SupplyMinimumStockReachedIntegrationEvent via RabbitMQ: {}", event);
        applicationEventPublisher.publishEvent(event);
    }

    @RabbitHandler
    public void handle(PurchaseOrderCreatedIntegrationEvent event) {
        log.info("Received PurchaseOrderCreatedIntegrationEvent via RabbitMQ: {}", event);
        applicationEventPublisher.publishEvent(event);
    }

    @RabbitHandler
    public void handle(PurchaseOrderStatusChangedIntegrationEvent event) {
        log.info("Received PurchaseOrderStatusChangedIntegrationEvent via RabbitMQ: {}", event);
        applicationEventPublisher.publishEvent(event);
    }

    @RabbitHandler(isDefault = true)
    public void handleDefault(Object event) {
        log.warn("Received unexpected event on operations queue: {}", event);
    }
}
