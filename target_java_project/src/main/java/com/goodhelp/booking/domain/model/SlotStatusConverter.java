package com.goodhelp.booking.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for SlotStatus enum.
 * Maps enum to integer database values for compatibility with existing schema.
 */
@Converter(autoApply = true)
public class SlotStatusConverter implements AttributeConverter<SlotStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SlotStatus status) {
        return status == null ? null : status.getValue();
    }

    @Override
    public SlotStatus convertToEntityAttribute(Integer value) {
        return value == null ? null : SlotStatus.fromValue(value);
    }
}
