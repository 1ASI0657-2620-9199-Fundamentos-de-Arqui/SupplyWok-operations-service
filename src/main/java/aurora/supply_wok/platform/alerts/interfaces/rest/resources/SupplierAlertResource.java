package aurora.supply_wok.platform.alerts.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Schema(
    name = "SupplierAlertResponse",
    description = "Supplier Alert details response payload",
    example = "{\"id\": 1, \"severity\": \"MEDIUM\", \"detail\": \"Supplier Alert Details Test\", \"status\": \"PENDING\", \"createdAt\": \"2026-06-19T03:30:00.000+00:00\", \"alertType\": \"SUPPLIER\"}"
)
public record SupplierAlertResource(
    @Schema(description = "Alert unique identifier", example = "1")
    Long id,

    @Schema(description = "Alert severity level", example = "MEDIUM")
    String severity,

    @Schema(description = "Alert details", example = "Supplier Alert Details Test")
    String detail,

    @Schema(description = "Alert status", example = "PENDING")
    String status,

    @Schema(description = "Alert creation timestamp")
    Date createdAt,

    @Schema(description = "Alert type", example = "SUPPLIER")
    String alertType
) {
}
