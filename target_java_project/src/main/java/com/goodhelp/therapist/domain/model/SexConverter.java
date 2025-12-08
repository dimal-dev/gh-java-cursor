package com.goodhelp.therapist.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for Sex enum.
 * Maps enum to integer database values for compatibility with existing schema.
 */
@Converter(autoApply = true)
public class SexConverter implements AttributeConverter<Sex, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Sex sex) {
        return sex == null ? null : sex.getValue();
    }

    @Override
    public Sex convertToEntityAttribute(Integer value) {
        return value == null ? null : Sex.fromValue(value);
    }
}
