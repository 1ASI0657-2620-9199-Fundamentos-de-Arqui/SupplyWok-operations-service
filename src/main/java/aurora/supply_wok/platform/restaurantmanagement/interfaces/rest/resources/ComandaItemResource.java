package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ComandaItemResponse", description = "Response payload for a comanda item")
public record ComandaItemResource(
        Long id,
        String productName,
        int quantity,
        String notes
) {
}
