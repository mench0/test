package com.tms.profilemanager.dto.validation;

import org.apache.commons.validator.routines.InetAddressValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IpAddressValidator implements ConstraintValidator<IpAddress, Object> {

    @Override
    public void initialize(IpAddress constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext cvContext) {
        if (!(value instanceof String)) {
            return false;
        }
        return InetAddressValidator.getInstance().isValid((String) value);
    }
}
