package aurora.supply_wok.platform.restaurantmanagement.interfaces.acl;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandaByIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetTableByIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.ComandaQueryService;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.TableQueryService;
import org.springframework.stereotype.Service;

@Service
public class RestaurantManagementContextFacade {

    private final ComandaQueryService comandaQueryService;
    private final TableQueryService tableQueryService;

    public RestaurantManagementContextFacade(ComandaQueryService comandaQueryService, TableQueryService tableQueryService) {
        this.comandaQueryService = comandaQueryService;
        this.tableQueryService = tableQueryService;
    }

    public String fetchComandaStatusById(Long comandaId) {
        try {
            var comanda = comandaQueryService.handle(new GetComandaByIdQuery(comandaId));
            return comanda.map(c -> c.getStatus().name()).orElse("");
        } catch (Exception e) {
            return "";
        }
    }

    public String fetchTableStatusById(Long tableId) {
        try {
            var table = tableQueryService.handle(new GetTableByIdQuery(tableId));
            return table.map(t -> t.getStatus().name()).orElse("");
        } catch (Exception e) {
            return "";
        }
    }

    public int fetchTableCapacity(Long tableId) {
        try {
            var table = tableQueryService.handle(new GetTableByIdQuery(tableId));
            return table.map(t -> t.getCapacity()).orElse(0);
        } catch (Exception e) {
            return 0;
        }
    }
}
