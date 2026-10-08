package aurora.supply_wok.platform.shared.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ messaging configuration for operations-service.
 */
@Configuration
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class MessagingConfiguration {

    public static final String EXCHANGE_NAME = "supplywok.events";
    public static final String QUEUE_NAME = "operations.events";
    public static final String ROUTING_KEY_INVENTORY = "inventory.#";
    public static final String ROUTING_KEY_PURCHASING = "purchasing.#";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue operationsQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding inventoryBinding(Queue operationsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(operationsQueue).to(eventsExchange).with(ROUTING_KEY_INVENTORY);
    }

    @Bean
    public Binding purchasingBinding(Queue operationsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(operationsQueue).to(eventsExchange).with(ROUTING_KEY_PURCHASING);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter("aurora.supply_wok.platform.*");
    }
}
