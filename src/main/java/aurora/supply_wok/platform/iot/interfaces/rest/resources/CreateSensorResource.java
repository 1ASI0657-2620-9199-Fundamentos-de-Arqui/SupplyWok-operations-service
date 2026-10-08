package aurora.supply_wok.platform.iot.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resource for creating a sensor.
 * Represents the request payload from the REST client.
 */
@Schema(
    name = "CreateSensorRequest",
    description = "Request payload for creating a new sensor",
    example = "{\"name\": \"Temperature Sensor 1\", \"minValue\": -20.0, \"maxValue\": 60.0, \"enabled\": true, \"lastValue\": 22.5, \"type\": \"Temperature\"}"
)
public record CreateSensorResource(
    @NotBlank
    @Schema(description = "Sensor name", example = "Temperature Sensor 1", minLength = 1, maxLength = 50)
    String name,

    @NotNull
    @Schema(description = "Minimum value the sensor can record", example = "-20.0")
    Double minValue,

    @NotNull
    @Schema(description = "Maximum value the sensor can record", example = "60.0")
    Double maxValue,

    @NotNull
    @Schema(description = "Whether the sensor is active", example = "true")
    Boolean enabled,

    @Schema(description = "Last recorded value", example = "22.5")
    Double lastValue,

    @NotBlank
    @Schema(description = "Sensor type", example = "Temperature", allowableValues = {"Temperature", "Humidity", "Weight"})
    String type
) {
}
