package com.goodhelp.therapist.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for SenderType enum.
 * Stores as integer in database for compatibility with PHP schema.
 */
@Converter(autoApply = true)
public class SenderTypeConverter implements AttributeConverter<SenderType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SenderType senderType) {
        return senderType != null ? senderType.getValue() : null;
    }

    @Override
    public SenderType convertToEntityAttribute(Integer value) {
        return value != null ? SenderType.fromValue(value) : null;
    }
}

