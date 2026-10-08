package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UpdateComandaStatusRequest", description = "Request payload for updating comanda status")
public record UpdateComandaStatusResource(
        @NotBlank @Schema(description = "New status", example = "SENT_TO_KITCHEN") String status
) {
}
