package com.goodhelp.billing.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for {@link OrderState}.
 */
@Converter(autoApply = true)
public class OrderStateConverter implements AttributeConverter<OrderState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(OrderState attribute) {
        return attribute != null ? attribute.getValue() : OrderState.CREATED.getValue();
    }

    @Override
    public OrderState convertToEntityAttribute(Integer dbData) {
        return dbData != null ? OrderState.fromValue(dbData) : OrderState.CREATED;
    }
}

