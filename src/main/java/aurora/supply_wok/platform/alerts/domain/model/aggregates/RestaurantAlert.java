package aurora.supply_wok.platform.alerts.domain.model.aggregates;

import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Entity
@DiscriminatorValue("RESTAURANT")
public class RestaurantAlert extends Alert {

    @Getter
    @Column(name = "restaurant_id", nullable = true)
    private Long restaurantId;

    @Getter
    @Column(name = "sensor_id", nullable = true)
    private Long sensorId;

    @Getter
    @NotBlank
    @Column(name = "sensor_name", nullable = true)
    private String sensorName;

    public RestaurantAlert() {
        super();
    }

    public RestaurantAlert(Long restaurantId, EAlertSeverity severity, String detail, Long sensorId, String sensorName) {
        super(severity, detail);
        this.restaurantId = restaurantId;
        this.sensorId = sensorId;
        this.sensorName = sensorName;
    }

    public RestaurantAlert(EAlertSeverity severity, String detail, Long sensorId, String sensorName) {
        this(1L, severity, detail, sensorId, sensorName);
    }

    public RestaurantAlert(CreateRestaurantAlertCommand command) {
        super(command.severity(), command.detail());
        this.restaurantId = command.restaurantId() != null ? command.restaurantId() : 1L;
        this.sensorId = command.sensorId() == null ? 0L : command.sensorId();
        this.sensorName = command.sensorId() == null ? "System" : "Sensor " + command.sensorId();
    }
}
