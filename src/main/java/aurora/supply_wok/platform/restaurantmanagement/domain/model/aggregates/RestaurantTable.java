package aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.TableCreatedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.TableStatusChangedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.TableUpdatedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;
import aurora.supply_wok.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Table(name = "restaurant_tables", uniqueConstraints = {
        @UniqueConstraint(name = "uk_table_restaurant_number", columnNames = {"restaurant_id", "table_number"})
})
public class RestaurantTable extends AuditableAbstractAggregateRoot<RestaurantTable> {

    @Getter
    @NotNull
    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Getter
    @NotNull
    @Column(name = "table_number", nullable = false)
    private int number;

    @Getter
    @NotNull
    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Getter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ETableStatus status;

    public RestaurantTable() {
    }

    public RestaurantTable(Long restaurantId, int number, int capacity) {
        this.restaurantId = restaurantId;
        this.number = number;
        this.capacity = capacity;
        this.status = ETableStatus.AVAILABLE;
    }

    public RestaurantTable(int number, int capacity) {
        this(1L, number, capacity);
    }

    public RestaurantTable(CreateTableCommand command) {
        this.restaurantId = command.restaurantId() != null ? command.restaurantId() : 1L;
        this.number = command.number();
        this.capacity = command.capacity();
        this.status = ETableStatus.AVAILABLE;
    }

    public RestaurantTable updateStatus(UpdateTableStatusCommand command) {
        var previousStatus = this.status;
        this.status = command.status();
        if (previousStatus != this.status) {
            addDomainEvent(TableStatusChangedEvent.from(this, previousStatus));
        }
        return this;
    }

    public RestaurantTable update(UpdateTableCommand command) {
        this.number = command.number();
        this.capacity = command.capacity();
        addDomainEvent(TableUpdatedEvent.from(this));
        return this;
    }

    public void onCreated() {
        addDomainEvent(TableCreatedEvent.from(this));
    }
}
