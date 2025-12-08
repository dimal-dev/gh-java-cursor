package com.goodhelp.booking.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for PriceState enum.
 * Maps enum to integer database values for compatibility with existing schema.
 */
@Converter(autoApply = true)
public class PriceStateConverter implements AttributeConverter<PriceState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PriceState state) {
        return state == null ? null : state.getValue();
    }

    @Override
    public PriceState convertToEntityAttribute(Integer value) {
        return value == null ? null : PriceState.fromValue(value);
    }
}
