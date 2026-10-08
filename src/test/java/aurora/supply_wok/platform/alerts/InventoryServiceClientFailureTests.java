package aurora.supply_wok.platform.alerts;

import aurora.supply_wok.platform.alerts.infrastructure.clients.InventoryServiceClient;
import aurora.supply_wok.platform.alerts.infrastructure.clients.InventoryServiceUnavailableException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryServiceClientFailureTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryServiceClient inventoryServiceClient;

    @Value("${authorization.jwt.secret}")
    private String jwtSecret;

    private String validToken;

    @BeforeEach
    void setUp() {
        var key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        validToken = Jwts.builder()
                .subject("test@example.com")
                .claim("roles", List.of("RESTAURANT"))
                .claim("userId", 1L)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    @Test
    void createAlert_whenInventoryServiceFails_returns503ServiceUnavailable() throws Exception {
        // Arrange
        Mockito.when(inventoryServiceClient.getTotalSupplyStock())
                .thenThrow(new InventoryServiceUnavailableException("Connection refused to inventory service"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/restaurant/alerts")
                        .header("Authorization", "Bearer " + validToken)
                        .param("sensorId", "1"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("SERVICE_UNAVAILABLE"));
    }

    @Test
    void checkInventory_whenInventoryServiceFails_returns503ServiceUnavailable() throws Exception {
        // Arrange
        Mockito.when(inventoryServiceClient.getTotalSupplyStock())
                .thenThrow(new InventoryServiceUnavailableException("Inventory service timeout"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/restaurant/alerts/inventory-check")
                        .header("Authorization", "Bearer " + validToken)
                        .param("sensorId", "1"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("SERVICE_UNAVAILABLE"));
    }

    @Test
    void createAlert_whenUnauthenticated_returns401Unauthorized() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(post("/api/v1/restaurant/alerts")
                        .param("sensorId", "1"))
                .andExpect(status().isUnauthorized());
    }
}
