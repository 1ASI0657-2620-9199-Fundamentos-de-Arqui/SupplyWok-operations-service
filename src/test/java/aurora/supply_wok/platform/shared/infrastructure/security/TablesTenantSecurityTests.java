package aurora.supply_wok.platform.shared.infrastructure.security;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
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
class TablesTenantSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantTableRepository tableRepository;

    private String tokenRestaurantA;
    private String tokenRestaurantB;
    private String tokenSupplier;

    @BeforeEach
    void setUp() {
        tableRepository.deleteAll();

        tokenRestaurantA = TestSecurityHelper.createToken(10L, "restA@supplywok.com", List.of("RESTAURANT"));
        tokenRestaurantB = TestSecurityHelper.createToken(20L, "restB@supplywok.com", List.of("RESTAURANT"));
        tokenSupplier = TestSecurityHelper.createToken(30L, "supplier@supplywok.com", List.of("SUPPLIER"));
    }

    @Test
    void createTable_whenCallerIsUnauthenticated_returnsUnauthorized() throws Exception {
        // Arrange
        var request = post("/api/v1/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"number": 1, "capacity": 4}
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void createTable_whenCallerHasWrongRole_returnsForbidden() throws Exception {
        // Arrange
        var request = post("/api/v1/tables")
                .header("Authorization", "Bearer " + tokenSupplier)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"number": 1, "capacity": 4}
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isForbidden());
    }

    @Test
    void getTableById_whenResourceBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var tableB = tableRepository.save(new RestaurantTable(20L, 5, 4));
        var request = get("/api/v1/tables/" + tableB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void updateTable_whenResourceBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var tableB = tableRepository.save(new RestaurantTable(20L, 5, 4));
        var request = put("/api/v1/tables/" + tableB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"number": 6, "capacity": 6}
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void deleteTable_whenResourceBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var tableB = tableRepository.save(new RestaurantTable(20L, 5, 4));
        var request = delete("/api/v1/tables/" + tableB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void getAllTables_whenMultipleTenantsExist_returnsOnlyCallerResources() throws Exception {
        // Arrange
        tableRepository.save(new RestaurantTable(10L, 1, 4));
        tableRepository.save(new RestaurantTable(10L, 2, 2));
        tableRepository.save(new RestaurantTable(20L, 1, 6));

        var request = get("/api/v1/tables")
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].number").value(1))
                .andExpect(jsonPath("$[1].number").value(2));
    }

    @Test
    void createTable_whenBodyContainsDifferentRestaurantId_usesTenantIdFromToken() throws Exception {
        // Arrange
        var request = post("/api/v1/tables")
                .header("Authorization", "Bearer " + tokenRestaurantA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"number": 1, "capacity": 4, "restaurantId": 999}
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isCreated());
        var savedTables = tableRepository.findAllByRestaurantId(10L);
        assertThat(savedTables).hasSize(1);
        assertThat(savedTables.get(0).getRestaurantId()).isEqualTo(10L);
        assertThat(tableRepository.findAllByRestaurantId(999L)).isEmpty();
    }
}
