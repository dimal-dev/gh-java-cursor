package com.goodhelp.therapist.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for MessageStatus enum.
 * Stores as integer in database for compatibility with PHP schema.
 */
@Converter(autoApply = true)
public class MessageStatusConverter implements AttributeConverter<MessageStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(MessageStatus status) {
        return status != null ? status.getValue() : null;
    }

    @Override
    public MessageStatus convertToEntityAttribute(Integer value) {
        return value != null ? MessageStatus.fromValue(value) : null;
    }
}

