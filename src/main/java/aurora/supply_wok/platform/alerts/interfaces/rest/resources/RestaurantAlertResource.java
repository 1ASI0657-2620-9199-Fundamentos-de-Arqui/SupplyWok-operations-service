package aurora.supply_wok.platform.alerts.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Schema(
    name = "RestaurantAlertResponse",
    description = "Restaurant Alert details response payload",
    example = "{\"id\": 2, \"severity\": \"CRITICAL\", \"detail\": \"Critical heat alert from sensor\", \"status\": \"PENDING\", \"createdAt\": \"2026-06-19T03:36:24.433Z\", \"alertType\": \"RESTAURANT\", \"sensorId\": 123, \"sensorName\": \"Main Boiler\"}"
)
public record RestaurantAlertResource(
    @Schema(description = "Alert unique identifier", example = "2")
    Long id,

    @Schema(description = "Alert severity level", example = "CRITICAL")
    String severity,

    @Schema(description = "Alert details", example = "Critical heat alert from sensor")
    String detail,

    @Schema(description = "Alert status", example = "PENDING")
    String status,

    @Schema(description = "Alert creation timestamp")
    Date createdAt,

    @Schema(description = "Alert type", example = "RESTAURANT")
    String alertType,

    @Schema(description = "Sensor identifier", example = "123")
    Long sensorId,

    @Schema(description = "Sensor name", example = "Main Boiler")
    String sensorName
) {
}
