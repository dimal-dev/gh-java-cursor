package com.goodhelp.therapist.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for TherapistRole enum.
 * Maps enum to integer database values for compatibility with existing schema.
 */
@Converter(autoApply = true)
public class TherapistRoleConverter implements AttributeConverter<TherapistRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TherapistRole role) {
        return role == null ? null : role.getValue();
    }

    @Override
    public TherapistRole convertToEntityAttribute(Integer value) {
        return value == null ? null : TherapistRole.fromValue(value);
    }
}
