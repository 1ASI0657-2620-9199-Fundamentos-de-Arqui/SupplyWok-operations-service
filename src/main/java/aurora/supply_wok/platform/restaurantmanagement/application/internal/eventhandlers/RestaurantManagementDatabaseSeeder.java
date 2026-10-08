package aurora.supply_wok.platform.restaurantmanagement.application.internal.eventhandlers;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.TableCommandService;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.RestaurantTableRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RestaurantManagementDatabaseSeeder {
    private final RestaurantTableRepository restaurantTableRepository;
    private final TableCommandService tableCommandService;

    public RestaurantManagementDatabaseSeeder(
            RestaurantTableRepository restaurantTableRepository,
            TableCommandService tableCommandService) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.tableCommandService = tableCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void on(ApplicationReadyEvent event) {
        if (restaurantTableRepository.findAll().isEmpty()) {
            log.info("Seeding restaurant tables...");
            tableCommandService.handle(new CreateTableCommand(1, 4));
            tableCommandService.handle(new CreateTableCommand(2, 2));
            log.info("Restaurant tables seeded successfully.");
        }
    }
}
