package com.goodhelp.user.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for ConsultationState enum.
 * Converts between enum and integer database values.
 */
@Converter(autoApply = true)
public class ConsultationStateConverter implements AttributeConverter<ConsultationState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ConsultationState state) {
        return state == null ? null : state.getValue();
    }

    @Override
    public ConsultationState convertToEntityAttribute(Integer value) {
        return value == null ? null : ConsultationState.fromValue(value);
    }
}

