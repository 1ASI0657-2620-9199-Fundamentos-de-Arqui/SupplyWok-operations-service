package aurora.supply_wok.platform.shared.infrastructure.security;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.ComandaRepository;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.RestaurantTableRepository;
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
class ComandasTenantSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantTableRepository tableRepository;

    @Autowired
    private ComandaRepository comandaRepository;

    private String tokenRestaurantA;
    private String tokenRestaurantB;
    private String tokenSupplier;

    @BeforeEach
    void setUp() {
        comandaRepository.deleteAll();
        tableRepository.deleteAll();

        tokenRestaurantA = TestSecurityHelper.createToken(10L, "restA@supplywok.com", List.of("RESTAURANT"));
        tokenRestaurantB = TestSecurityHelper.createToken(20L, "restB@supplywok.com", List.of("RESTAURANT"));
        tokenSupplier = TestSecurityHelper.createToken(30L, "supplier@supplywok.com", List.of("SUPPLIER"));
    }

    @Test
    void createComanda_whenCallerIsUnauthenticated_returnsUnauthorized() throws Exception {
        // Arrange
        var request = post("/api/v1/comandas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tableId": 1}
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void createComanda_whenCallerHasWrongRole_returnsForbidden() throws Exception {
        // Arrange
        var request = post("/api/v1/comandas")
                .header("Authorization", "Bearer " + tokenSupplier)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tableId": 1}
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isForbidden());
    }

    @Test
    void createComanda_whenTableBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var tableB = tableRepository.save(new RestaurantTable(20L, 5, 4));
        var request = post("/api/v1/comandas")
                .header("Authorization", "Bearer " + tokenRestaurantA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tableId": %d}
                        """.formatted(tableB.getId()));

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void getComandaById_whenResourceBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var tableB = tableRepository.save(new RestaurantTable(20L, 5, 4));
        var comandaB = comandaRepository.save(new Comanda(20L, tableB.getId()));

        var request = get("/api/v1/comandas/" + comandaB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void getAllComandas_whenMultipleTenantsExist_returnsOnlyCallerResources() throws Exception {
        // Arrange
        var tableA = tableRepository.save(new RestaurantTable(10L, 1, 4));
        var tableB = tableRepository.save(new RestaurantTable(20L, 2, 4));

        comandaRepository.save(new Comanda(10L, tableA.getId()));
        comandaRepository.save(new Comanda(10L, tableA.getId()));
        comandaRepository.save(new Comanda(20L, tableB.getId()));

        var request = get("/api/v1/comandas")
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getComandasByTableId_whenTableBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var tableB = tableRepository.save(new RestaurantTable(20L, 2, 4));
        comandaRepository.save(new Comanda(20L, tableB.getId()));

        var request = get("/api/v1/comandas/table/" + tableB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void updateComandaStatus_whenResourceBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var tableB = tableRepository.save(new RestaurantTable(20L, 2, 4));
        var comandaB = comandaRepository.save(new Comanda(20L, tableB.getId()));

        var request = put("/api/v1/comandas/" + comandaB.getId() + "/status")
                .header("Authorization", "Bearer " + tokenRestaurantA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status": "SENT_TO_KITCHEN"}
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void addComandaItem_whenResourceBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var tableB = tableRepository.save(new RestaurantTable(20L, 2, 4));
        var comandaB = comandaRepository.save(new Comanda(20L, tableB.getId()));

        var request = post("/api/v1/comandas/" + comandaB.getId() + "/items")
                .header("Authorization", "Bearer " + tokenRestaurantA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"productName": "Wok Rice", "quantity": 2, "notes": "No spicy"}
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void deleteComanda_whenResourceBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var tableB = tableRepository.save(new RestaurantTable(20L, 2, 4));
        var comandaB = comandaRepository.save(new Comanda(20L, tableB.getId()));

        var request = delete("/api/v1/comandas/" + comandaB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
        assertThat(comandaRepository.findById(comandaB.getId())).isPresent();
    }
}
