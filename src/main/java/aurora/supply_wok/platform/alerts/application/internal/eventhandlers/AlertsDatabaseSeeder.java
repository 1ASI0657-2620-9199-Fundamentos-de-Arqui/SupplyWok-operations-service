package aurora.supply_wok.platform.alerts.application.internal.eventhandlers;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertsDatabaseSeeder {
    private final AlertPersistenceRepository alertPersistenceRepository;
    private final AlertCommandService alertCommandService;

    public AlertsDatabaseSeeder(
            AlertPersistenceRepository alertPersistenceRepository,
            AlertCommandService alertCommandService) {
        this.alertPersistenceRepository = alertPersistenceRepository;
        this.alertCommandService = alertCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    public void on(ApplicationReadyEvent event) {
        if (alertPersistenceRepository.findAll().isEmpty()) {
            log.info("Seeding alerts...");
            alertCommandService.handle(new CreateRestaurantAlertCommand(
                    EAlertSeverity.CRITICAL,
                    "Temperatura alta detectada en refrigerador de verduras",
                    1L
            ));
            log.info("Alerts seeded successfully.");
        }
    }
}
