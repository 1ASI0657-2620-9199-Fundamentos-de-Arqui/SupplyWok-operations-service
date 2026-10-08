package aurora.supply_wok.platform.alerts.application.internal.queryservices;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAlertByIdQuery;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllRestaurantAlertsQuery;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllSupplierAlertsQuery;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertQueryServiceImplTests {

    @Mock
    private AlertPersistenceRepository alertPersistenceRepository;

    @InjectMocks
    private AlertQueryServiceImpl alertQueryService;

    @Test
    void handleGetAlertById_whenInvoked_delegatesToRepositoryFindById() {
        // Arrange
        var alert = new RestaurantAlert(EAlertSeverity.HIGH, "Warning", 1L, "S1");
        when(alertPersistenceRepository.findById(10L)).thenReturn(Optional.of(alert));

        // Act
        Optional<Alert> result = alertQueryService.handle(new GetAlertByIdQuery(10L));

        // Assert
        assertThat(result).containsSame(alert);
        verify(alertPersistenceRepository).findById(10L);
    }

    @Test
    void handleGetAllSupplierAlerts_whenInvoked_delegatesToRepositoryFindAllSupplierAlerts() {
        // Arrange
        var expectedList = List.of(new SupplierAlert(EAlertSeverity.MEDIUM, "Delayed"));
        when(alertPersistenceRepository.findAllSupplierAlerts()).thenReturn(expectedList);

        // Act
        List<SupplierAlert> result = alertQueryService.handle(new GetAllSupplierAlertsQuery());

        // Assert
        assertThat(result).isSameAs(expectedList);
        verify(alertPersistenceRepository).findAllSupplierAlerts();
    }

    @Test
    void handleGetAllRestaurantAlerts_whenInvoked_delegatesToRepositoryFindAllRestaurantAlerts() {
        // Arrange
        var expectedList = List.of(new RestaurantAlert(EAlertSeverity.LOW, "Stock low", 0L, "System"));
        when(alertPersistenceRepository.findAllRestaurantAlerts()).thenReturn(expectedList);

        // Act
        List<RestaurantAlert> result = alertQueryService.handle(new GetAllRestaurantAlertsQuery());

        // Assert
        assertThat(result).isSameAs(expectedList);
        verify(alertPersistenceRepository).findAllRestaurantAlerts();
    }
}
