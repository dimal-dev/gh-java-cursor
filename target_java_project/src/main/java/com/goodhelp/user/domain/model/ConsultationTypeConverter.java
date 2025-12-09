package com.goodhelp.user.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for ConsultationType enum.
 * Converts between enum and integer database values.
 */
@Converter(autoApply = true)
public class ConsultationTypeConverter implements AttributeConverter<ConsultationType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ConsultationType type) {
        return type == null ? null : type.getValue();
    }

    @Override
    public ConsultationType convertToEntityAttribute(Integer value) {
        return value == null ? null : ConsultationType.fromValue(value);
    }
}

