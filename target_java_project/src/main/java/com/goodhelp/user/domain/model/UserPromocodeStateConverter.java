package com.goodhelp.user.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for {@link UserPromocodeState}.
 */
@Converter(autoApply = true)
public class UserPromocodeStateConverter implements AttributeConverter<UserPromocodeState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserPromocodeState attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public UserPromocodeState convertToEntityAttribute(Integer dbData) {
        return dbData != null ? UserPromocodeState.fromValue(dbData) : UserPromocodeState.APPLIED;
    }
}

