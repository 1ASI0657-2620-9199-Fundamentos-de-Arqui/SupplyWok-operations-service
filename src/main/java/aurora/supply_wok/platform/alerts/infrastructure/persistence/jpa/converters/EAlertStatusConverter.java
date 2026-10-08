package aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.converters;

import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EAlertStatusConverter implements AttributeConverter<EAlertStatus, String> {

    @Override
    public String convertToDatabaseColumn(EAlertStatus attribute) {
        return attribute == null ? null : attribute.toStoredValue();
    }

    @Override
    public EAlertStatus convertToEntityAttribute(String dbData) {
        return EAlertStatus.fromStoredValue(dbData);
    }
}
