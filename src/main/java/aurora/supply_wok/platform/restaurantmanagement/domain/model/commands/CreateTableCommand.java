package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

public record CreateTableCommand(Long restaurantId, int number, int capacity) {
    public CreateTableCommand(int number, int capacity) {
        this(1L, number, capacity);
    }
}
