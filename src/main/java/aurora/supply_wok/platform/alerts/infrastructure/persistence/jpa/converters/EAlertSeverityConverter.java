package aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.converters;

import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EAlertSeverityConverter implements AttributeConverter<EAlertSeverity, String> {

    @Override
    public String convertToDatabaseColumn(EAlertSeverity attribute) {
        return attribute == null ? null : attribute.toStoredValue();
    }

    @Override
    public EAlertSeverity convertToEntityAttribute(String dbData) {
        return EAlertSeverity.fromStoredValue(dbData);
    }
}
