package aurora.supply_wok.platform.restaurantmanagement.application.internal.eventhandlers;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.ComandaCreatedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.ComandaStatusChangedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.EComandaStatus;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.RestaurantTableRepository;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Keeps table occupancy aligned with comanda lifecycle events.
 */
@Service
@Slf4j
public class ComandaTableOccupancyEventHandler {

    private final RestaurantTableRepository tableRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ComandaTableOccupancyEventHandler(RestaurantTableRepository tableRepository,
                                             DomainEventPublisher domainEventPublisher) {
        this.tableRepository = tableRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @EventListener
    public void on(ComandaCreatedEvent event) {
        tableRepository.findById(event.tableId()).ifPresentOrElse(table -> {
            table.updateStatus(new aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand(
                    table.getId(),
                    ETableStatus.OCCUPIED
            ));
            tableRepository.save(table);
            domainEventPublisher.publishAndClear(table);
        }, () -> log.warn("Could not mark table {} as occupied for comanda {}", event.tableId(), event.comandaId()));
    }

    @EventListener
    public void on(ComandaStatusChangedEvent event) {
        if (event.currentStatus() != EComandaStatus.CLOSED && event.currentStatus() != EComandaStatus.SERVED) {
            return;
        }

        tableRepository.findById(event.tableId()).ifPresentOrElse(table -> {
            table.updateStatus(new aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand(
                    table.getId(),
                    ETableStatus.AVAILABLE
            ));
            tableRepository.save(table);
            domainEventPublisher.publishAndClear(table);
        }, () -> log.warn("Could not release table {} for comanda {}", event.tableId(), event.comandaId()));
    }
}
