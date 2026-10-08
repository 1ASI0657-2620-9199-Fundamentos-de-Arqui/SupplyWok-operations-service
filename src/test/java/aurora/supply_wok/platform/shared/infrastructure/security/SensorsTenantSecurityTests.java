package aurora.supply_wok.platform.shared.infrastructure.security;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import aurora.supply_wok.platform.iot.infrastructure.persistence.jpa.repositories.SensorPersistenceRepository;
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
class SensorsTenantSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SensorPersistenceRepository sensorRepository;

    private String tokenRestaurantA;
    private String tokenRestaurantB;
    private String tokenSupplier;

    @BeforeEach
    void setUp() {
        sensorRepository.deleteAll();

        tokenRestaurantA = TestSecurityHelper.createToken(10L, "restA@supplywok.com", List.of("RESTAURANT"));
        tokenRestaurantB = TestSecurityHelper.createToken(20L, "restB@supplywok.com", List.of("RESTAURANT"));
        tokenSupplier = TestSecurityHelper.createToken(30L, "supplier@supplywok.com", List.of("SUPPLIER"));
    }

    @Test
    void createSensor_whenCallerIsUnauthenticated_returnsUnauthorized() throws Exception {
        // Arrange
        var request = post("/api/v1/sensors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Temperature Sensor",
                            "minValue": -20.0,
                            "maxValue": 60.0,
                            "enabled": true,
                            "lastValue": 22.5,
                            "type": "Temperature"
                        }
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void createSensor_whenCallerHasWrongRole_returnsForbidden() throws Exception {
        // Arrange
        var request = post("/api/v1/sensors")
                .header("Authorization", "Bearer " + tokenSupplier)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Temperature Sensor",
                            "minValue": -20.0,
                            "maxValue": 60.0,
                            "enabled": true,
                            "lastValue": 22.5,
                            "type": "Temperature"
                        }
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isForbidden());
    }

    @Test
    void createSensor_whenCalled_assignsCallerRestaurantId() throws Exception {
        // Arrange
        var request = post("/api/v1/sensors")
                .header("Authorization", "Bearer " + tokenRestaurantA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Temperature Sensor",
                            "minValue": -20.0,
                            "maxValue": 60.0,
                            "enabled": true,
                            "lastValue": 22.5,
                            "type": "Temperature"
                        }
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isCreated());
        var sensorsA = sensorRepository.findAllByRestaurantId(10L);
        assertThat(sensorsA).hasSize(1);
        assertThat(sensorsA.get(0).getRestaurantId()).isEqualTo(10L);
    }

    @Test
    void getSensorById_whenResourceBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var sensorB = sensorRepository.save(new Sensor(20L, "Freezer B", -20.0, 0.0, true, -10.0, ESensorType.Temperature));

        var request = get("/api/v1/sensors/" + sensorB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void getAllSensors_whenMultipleTenantsExist_returnsOnlyCallerResources() throws Exception {
        // Arrange
        sensorRepository.save(new Sensor(10L, "Freezer 1", -20.0, 0.0, true, -10.0, ESensorType.Temperature));
        sensorRepository.save(new Sensor(10L, "Freezer 2", -20.0, 0.0, true, -12.0, ESensorType.Temperature));
        sensorRepository.save(new Sensor(20L, "Freezer B", -20.0, 0.0, true, -5.0, ESensorType.Temperature));

        var request = get("/api/v1/sensors")
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateSensor_whenResourceBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var sensorB = sensorRepository.save(new Sensor(20L, "Freezer B", -20.0, 0.0, true, -10.0, ESensorType.Temperature));

        var request = put("/api/v1/sensors/" + sensorB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Updated Sensor",
                            "minValue": -20.0,
                            "maxValue": 0.0,
                            "enabled": true,
                            "lastValue": -5.0,
                            "type": "Temperature"
                        }
                        """);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void deleteSensor_whenResourceBelongsToAnotherTenant_returnsNotFound() throws Exception {
        // Arrange
        var sensorB = sensorRepository.save(new Sensor(20L, "Freezer B", -20.0, 0.0, true, -10.0, ESensorType.Temperature));

        var request = delete("/api/v1/sensors/" + sensorB.getId())
                .header("Authorization", "Bearer " + tokenRestaurantA);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
        assertThat(sensorRepository.findById(sensorB.getId())).isPresent();
    }
}
