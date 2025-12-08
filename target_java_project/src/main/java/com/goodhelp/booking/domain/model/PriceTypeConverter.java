package com.goodhelp.booking.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for PriceType enum.
 * Maps enum to integer database values for compatibility with existing schema.
 */
@Converter(autoApply = true)
public class PriceTypeConverter implements AttributeConverter<PriceType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PriceType type) {
        return type == null ? null : type.getValue();
    }

    @Override
    public PriceType convertToEntityAttribute(Integer value) {
        return value == null ? null : PriceType.fromValue(value);
    }
}
