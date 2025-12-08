package com.goodhelp.therapist.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for PriceType enum.
 */
@Converter(autoApply = true)
public class PriceTypeConverter implements AttributeConverter<PriceType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PriceType priceType) {
        return priceType != null ? priceType.getCode() : null;
    }

    @Override
    public PriceType convertToEntityAttribute(Integer code) {
        return code != null ? PriceType.fromCode(code) : null;
    }
}

