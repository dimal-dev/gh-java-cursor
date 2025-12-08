package com.goodhelp.therapist.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for TherapistStatus enum.
 * Maps enum to integer database values for compatibility with existing schema.
 */
@Converter(autoApply = true)
public class TherapistStatusConverter implements AttributeConverter<TherapistStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TherapistStatus status) {
        return status == null ? null : status.getValue();
    }

    @Override
    public TherapistStatus convertToEntityAttribute(Integer value) {
        return value == null ? null : TherapistStatus.fromValue(value);
    }
}
