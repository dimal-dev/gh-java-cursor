package com.goodhelp.therapist.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for PriceState enum.
 */
@Converter(autoApply = true)
public class PriceStateConverter implements AttributeConverter<PriceState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PriceState priceState) {
        return priceState != null ? priceState.getCode() : null;
    }

    @Override
    public PriceState convertToEntityAttribute(Integer code) {
        return code != null ? PriceState.fromCode(code) : null;
    }
}

