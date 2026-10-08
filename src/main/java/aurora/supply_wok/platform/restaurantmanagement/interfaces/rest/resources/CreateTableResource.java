package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CreateTableRequest", description = "Request payload for creating a new table")
public record CreateTableResource(
        @NotNull @Schema(description = "Table number", example = "1") Integer number,
        @NotNull @Schema(description = "Table capacity (seats)", example = "4") Integer capacity
) {
}
