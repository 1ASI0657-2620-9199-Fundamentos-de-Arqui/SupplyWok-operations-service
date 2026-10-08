package aurora.supply_wok.platform.iot.application.internal.eventhandlers;

import aurora.supply_wok.platform.iot.application.commandservices.SensorCommandService;
import aurora.supply_wok.platform.iot.domain.model.commands.CreateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import aurora.supply_wok.platform.iot.infrastructure.persistence.jpa.repositories.SensorPersistenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IotDatabaseSeeder {
    private final SensorPersistenceRepository sensorPersistenceRepository;
    private final SensorCommandService sensorCommandService;

    public IotDatabaseSeeder(
            SensorPersistenceRepository sensorPersistenceRepository,
            SensorCommandService sensorCommandService) {
        this.sensorPersistenceRepository = sensorPersistenceRepository;
        this.sensorCommandService = sensorCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void on(ApplicationReadyEvent event) {
        if (sensorPersistenceRepository.findAll().isEmpty()) {
            log.info("Seeding IoT sensors...");
            sensorCommandService.handle(new CreateSensorCommand(
                    "Sensor Temperatura Nevera 1",
                    1.0,
                    8.0,
                    true,
                    4.5,
                    ESensorType.Temperature
            ));
            log.info("IoT sensors seeded successfully.");
        }
    }
}
