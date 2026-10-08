package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TableResponse", description = "Response payload for a restaurant table")
public record TableResource(
        Long id,
        int number,
        int capacity,
        String status
) {
}
