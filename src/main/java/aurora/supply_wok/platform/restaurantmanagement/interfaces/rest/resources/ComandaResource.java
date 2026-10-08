package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ComandaResponse", description = "Response payload for a comanda")
public record ComandaResource(
        Long id,
        Long tableId,
        String status,
        List<ComandaItemResource> items
) {
}
