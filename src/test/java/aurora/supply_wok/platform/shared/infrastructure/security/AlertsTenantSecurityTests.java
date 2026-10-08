package aurora.supply_wok.platform.shared.infrastructure.security;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AlertsTenantSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlertPersistenceRepository alertRepository;

    private String tokenRestaurantA;
    private String tokenRestaurantB;
    private String tokenSupplierA;
    private String tokenSupplierB;

    @BeforeEach
    void setUp() {
        alertRepository.deleteAll();

        tokenRestaurantA = TestSecurityHelper.createToken(10L, "restA@supplywok.com", List.of("RESTAURANT"));
        tokenRestaurantB = TestSecurityHelper.createToken(20L, "restB@supplywok.com", List.of("RESTAURANT"));
        tokenSupplierA = TestSecurityHelper.createToken(30L, "suppA@supplywok.com", List.of("SUPPLIER"));
        tokenSupplierB = TestSecurityHelper.createToken(40L, "suppB@supplywok.com", List.of("SUPPLIER"));
    }

    // --- RESTAURANT ALERTS TESTS ---

    @Test
    void getRestaurantAlertById_whenCallerIsUnauthenticated_returnsUnauthorized() throws Exception {
        // Arrange
        var request = get("/api/v1/restaurant/alerts/1");

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void getRestaurantAlertById_whenCallerHasWrongRole_returnsForbidden() throws Exception {
        // Arrange
        var request = get("/api/v1/restaurant/alerts/1")
                .header("Authorization", "Bearer " + tokenSupplierA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isForbidden());
    }

    @Test
    void getRestaurantAlertById_whenAlertBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var alertB = alertRepository.save(new RestaurantAlert(20L, EAlertSeverity.HIGH, "Low stock for B", 1L, "Sensor 1"));
        var request = get("/api/v1/restaurant/alerts/" + alertB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void getAllRestaurantAlerts_whenMultipleTenantsExist_returnsOnlyCallerAlerts() throws Exception {
        // Arrange
        alertRepository.save(new RestaurantAlert(10L, EAlertSeverity.HIGH, "Alert 1", 1L, "Sensor 1"));
        alertRepository.save(new RestaurantAlert(10L, EAlertSeverity.LOW, "Alert 2", 2L, "Sensor 2"));
        alertRepository.save(new RestaurantAlert(20L, EAlertSeverity.HIGH, "Alert B", 3L, "Sensor 3"));

        var request = get("/api/v1/restaurant/alerts")
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void acknowledgeRestaurantAlert_whenAlertBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var alertB = alertRepository.save(new RestaurantAlert(20L, EAlertSeverity.HIGH, "Low stock for B", 1L, "Sensor 1"));
        var request = patch("/api/v1/restaurant/alerts/" + alertB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    // --- SUPPLIER ALERTS TESTS ---

    @Test
    void getSupplierAlertById_whenCallerIsUnauthenticated_returnsUnauthorized() throws Exception {
        // Arrange
        var request = get("/api/v1/supplier/alerts/1");

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void getSupplierAlertById_whenCallerHasWrongRole_returnsForbidden() throws Exception {
        // Arrange
        var request = get("/api/v1/supplier/alerts/1")
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isForbidden());
    }

    @Test
    void getSupplierAlertById_whenAlertBelongsToAnotherSupplier_returnsNotFound() throws Exception {
        // Arrange
        var alertB = alertRepository.save(new SupplierAlert("suppB@supplywok.com", EAlertSeverity.HIGH, "Order created for B"));
        var request = get("/api/v1/supplier/alerts/" + alertB.getId())
                .header("Authorization", "Bearer " + tokenSupplierA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void getAllSupplierAlerts_whenMultipleSuppliersExist_returnsOnlyCallerAlerts() throws Exception {
        // Arrange
        alertRepository.save(new SupplierAlert("suppA@supplywok.com", EAlertSeverity.HIGH, "Alert A1"));
        alertRepository.save(new SupplierAlert("suppA@supplywok.com", EAlertSeverity.MEDIUM, "Alert A2"));
        alertRepository.save(new SupplierAlert("suppB@supplywok.com", EAlertSeverity.HIGH, "Alert B1"));

        var request = get("/api/v1/supplier/alerts")
                .header("Authorization", "Bearer " + tokenSupplierA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void acknowledgeSupplierAlert_whenAlertBelongsToAnotherSupplier_returnsNotFound() throws Exception {
        // Arrange
        var alertB = alertRepository.save(new SupplierAlert("suppB@supplywok.com", EAlertSeverity.HIGH, "Order created for B"));
        var request = patch("/api/v1/supplier/alerts/" + alertB.getId())
                .header("Authorization", "Bearer " + tokenSupplierA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }
}
