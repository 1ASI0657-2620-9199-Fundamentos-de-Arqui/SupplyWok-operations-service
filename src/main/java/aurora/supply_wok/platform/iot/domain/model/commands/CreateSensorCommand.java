package aurora.supply_wok.platform.iot.domain.model.commands;

import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;

public record CreateSensorCommand(String name, double minValue, double maxValue, boolean enabled, double lastValue,ESensorType type) {
}
