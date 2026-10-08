package aurora.supply_wok.platform.iot.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for representing a sensor in HTTP responses.
 * Represents the response payload returned to the REST client.
 */
@Schema(
    name = "SensorResponse",
    description = "Sensor details response payload",
    example = "{\"id\": 1, \"name\": \"Temperature Sensor 1\", \"minValue\": -20.0, \"maxValue\": 60.0, \"enabled\": true, \"lastValue\": 22.5, \"type\": \"Temperature\"}"
)
public record SensorResource(
    @Schema(description = "Sensor unique identifier", example = "1")
    Long id,

    @Schema(description = "Sensor name", example = "Temperature Sensor 1")
    String name,

    @Schema(description = "Minimum value the sensor can record", example = "-20.0")
    double minValue,

    @Schema(description = "Maximum value the sensor can record", example = "60.0")
    double maxValue,

    @Schema(description = "Whether the sensor is active", example = "true")
    boolean enabled,

    @Schema(description = "Last recorded value", example = "22.5")
    double lastValue,

    @Schema(description = "Sensor type", example = "Temperature")
    String type
) {
}
