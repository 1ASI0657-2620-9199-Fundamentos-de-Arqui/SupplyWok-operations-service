package aurora.supply_wok.platform.iot.domain.model.aggregates;

import aurora.supply_wok.platform.iot.domain.model.commands.CreateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.commands.UpdateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.events.SensorCreatedEvent;
import aurora.supply_wok.platform.iot.domain.model.events.SensorThresholdBreachedEvent;
import aurora.supply_wok.platform.iot.domain.model.events.SensorUpdatedEvent;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import aurora.supply_wok.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * Sensor aggregate root.
 * Represents a physical or virtual sensor in the IoT domain.
 */
@Entity
@Table(name = "sensors")
public class Sensor extends AuditableAbstractAggregateRoot<Sensor> {

    @Getter
    @NotNull
    @NotBlank
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Getter
    @NotNull
    @Column(name = "min_value", nullable = false)
    private double minValue;

    @Getter
    @NotNull
    @Column(name = "max_value", nullable = false)
    private double maxValue;

    @Getter
    @NotNull
    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Getter
    @Column(name = "last_value", nullable = true)
    private double lastValue;

    @Getter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ESensorType type;

    /**
     * Default constructor for JPA.
     */
    public Sensor() {
    }

    /**
     * Full constructor for creating a Sensor.
     *
     * @param name      the name of the sensor
     * @param minValue  the minimum value the sensor can record
     * @param maxValue  the maximum value the sensor can record
     * @param enabled   whether the sensor is active
     * @param lastValue the last recorded value
     * @param type      the sensor type
     */
    public Sensor(String name, double minValue, double maxValue, boolean enabled, double lastValue, ESensorType type) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.enabled = enabled;
        this.lastValue = lastValue;
        this.type = type;
    }

    /**
     * Constructs a Sensor from a CreateSensorCommand.
     *
     * @param command the command containing initialization data
     */
    public Sensor(CreateSensorCommand command) {
        this.name = command.name();
        this.minValue = command.minValue();
        this.maxValue = command.maxValue();
        this.enabled = command.enabled();
        this.lastValue = command.lastValue();
        this.type = command.type();
    }

    /**
     * Updates the sensor attributes based on an UpdateSensorCommand.
     *
     * @param command the command containing update data
     * @return the updated Sensor instance
     */
    public Sensor updateSensor(UpdateSensorCommand command) {
        this.name = command.name();
        this.minValue = command.minValue();
        this.maxValue = command.maxValue();
        this.enabled = command.enabled();
        this.lastValue = command.lastValue();
        this.type = command.type();
        addDomainEvent(SensorUpdatedEvent.from(this));
        if (isThresholdBreached()) {
            addDomainEvent(SensorThresholdBreachedEvent.from(this));
        }
        return this;
    }

    public void onCreated() {
        addDomainEvent(SensorCreatedEvent.from(this));
        if (isThresholdBreached()) {
            addDomainEvent(SensorThresholdBreachedEvent.from(this));
        }
    }

    private boolean isThresholdBreached() {
        return enabled && (lastValue < minValue || lastValue > maxValue);
    }
}
