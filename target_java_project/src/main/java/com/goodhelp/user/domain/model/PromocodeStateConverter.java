package com.goodhelp.user.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for {@link PromocodeState}.
 */
@Converter(autoApply = true)
public class PromocodeStateConverter implements AttributeConverter<PromocodeState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PromocodeState attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public PromocodeState convertToEntityAttribute(Integer dbData) {
        return dbData != null ? PromocodeState.fromValue(dbData) : PromocodeState.INACTIVE;
    }
}

