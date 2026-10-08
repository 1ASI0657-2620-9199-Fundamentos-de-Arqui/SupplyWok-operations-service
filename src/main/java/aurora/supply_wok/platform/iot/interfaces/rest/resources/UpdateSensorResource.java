package aurora.supply_wok.platform.iot.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resource for updating a sensor.
 * Represents the request payload from the REST client.
 */
@Schema(
    name = "UpdateSensorRequest",
    description = "Request payload for updating an existing sensor",
    example = "{\"name\": \"Temperature Sensor 1 Updated\", \"minValue\": -20.0, \"maxValue\": 60.0, \"enabled\": true, \"lastValue\": 23.5, \"type\": \"Temperature\"}"
)
public record UpdateSensorResource(
    @NotBlank
    @Schema(description = "Sensor name", example = "Temperature Sensor 1 Updated", minLength = 1, maxLength = 50)
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

    @Schema(description = "Last recorded value", example = "23.5")
    Double lastValue,

    @NotBlank
    @Schema(description = "Sensor type", example = "Temperature", allowableValues = {"Temperature", "Humidity", "Weight"})
    String type
) {
}
