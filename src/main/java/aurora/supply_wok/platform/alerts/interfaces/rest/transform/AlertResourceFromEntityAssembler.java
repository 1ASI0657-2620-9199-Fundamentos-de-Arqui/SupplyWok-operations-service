package aurora.supply_wok.platform.alerts.interfaces.rest.transform;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.interfaces.rest.resources.RestaurantAlertResource;
import aurora.supply_wok.platform.alerts.interfaces.rest.resources.SupplierAlertResource;

import java.util.Locale;

public class AlertResourceFromEntityAssembler {

    public static RestaurantAlertResource toRestaurantResourceFromEntity(RestaurantAlert restaurantAlert) {
        return new RestaurantAlertResource(
                restaurantAlert.getId(),
                restaurantAlert.getSeverity().name().toUpperCase(Locale.ROOT),
                restaurantAlert.getDetail(),
                restaurantAlert.getStatus().name().toUpperCase(Locale.ROOT),
                restaurantAlert.getCreatedAt(),
                "RESTAURANT",
                restaurantAlert.getSensorId() == null || restaurantAlert.getSensorId() == 0L ? null : restaurantAlert.getSensorId(),
                restaurantAlert.getSensorName()
        );
    }

    public static SupplierAlertResource toSupplierResourceFromEntity(SupplierAlert supplierAlert) {
        return new SupplierAlertResource(
                supplierAlert.getId(),
                supplierAlert.getSeverity().name().toUpperCase(Locale.ROOT),
                supplierAlert.getDetail(),
                supplierAlert.getStatus().name().toUpperCase(Locale.ROOT),
                supplierAlert.getCreatedAt(),
                "SUPPLIER"
        );
    }
}
