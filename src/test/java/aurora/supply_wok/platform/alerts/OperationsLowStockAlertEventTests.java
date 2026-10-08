package aurora.supply_wok.platform.alerts;

import aurora.supply_wok.platform.alerts.application.queryservices.AlertQueryService;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllRestaurantAlertsQuery;
import aurora.supply_wok.platform.inventory.interfaces.events.SupplyMinimumStockReachedIntegrationEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OperationsLowStockAlertEventTests {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final AlertQueryService alertQueryService;

    @Autowired
    OperationsLowStockAlertEventTests(ApplicationEventPublisher applicationEventPublisher,
                                      AlertQueryService alertQueryService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.alertQueryService = alertQueryService;
    }

    @Test
    void onSupplyMinimumStockReached_whenEventPublished_createsRestaurantAlert() {
        // Arrange
        var event = new SupplyMinimumStockReachedIntegrationEvent(
                1L,
                "Test low stock rice",
                2,
                5
        );

        // Act
        applicationEventPublisher.publishEvent(event);
        var alerts = alertQueryService.handle(new GetAllRestaurantAlertsQuery());

        // Assert
        assertThat(alerts)
                .anySatisfy(alert -> {
                    assertThat(alert.getDetail())
                            .contains("Test low stock rice")
                            .contains("2/5");
                    assertThat(alert.getSensorId()).isZero();
                    assertThat(alert.getSensorName()).isEqualTo("System");
                });
    }
}
