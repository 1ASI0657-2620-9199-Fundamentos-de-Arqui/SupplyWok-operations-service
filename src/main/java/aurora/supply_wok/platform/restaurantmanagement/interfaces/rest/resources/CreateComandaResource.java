package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CreateComandaRequest", description = "Request payload for creating a new comanda")
public record CreateComandaResource(
        @NotNull @Schema(description = "Table ID to assign the comanda to", example = "1") Long tableId
) {
}
