package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "AddComandaItemRequest", description = "Request payload for adding an item to a comanda")
public record AddComandaItemResource(
        @NotBlank @Schema(description = "Name of the product", example = "Lomo Saltado") String productName,
        @NotNull @Schema(description = "Quantity ordered", example = "2") Integer quantity,
        @Schema(description = "Additional notes", example = "Sin cebolla") String notes
) {
}
