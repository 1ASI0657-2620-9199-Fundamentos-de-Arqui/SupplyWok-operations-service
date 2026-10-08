package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "UpdateTableRequest", description = "Request payload for updating a restaurant table")
public record UpdateTableResource(
        @NotNull @Schema(description = "Table number", example = "12") Integer number,
        @NotNull @Schema(description = "Table capacity (seats)", example = "4") Integer capacity
) {
}
