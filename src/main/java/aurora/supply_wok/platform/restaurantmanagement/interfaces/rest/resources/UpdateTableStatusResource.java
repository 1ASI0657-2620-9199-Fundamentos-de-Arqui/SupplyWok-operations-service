package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UpdateTableStatusRequest", description = "Request payload for updating table status")
public record UpdateTableStatusResource(
        @NotBlank @Schema(description = "New status", example = "OCCUPIED") String status
) {
}
