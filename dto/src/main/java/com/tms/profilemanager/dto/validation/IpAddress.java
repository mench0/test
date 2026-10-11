package com.tms.profilemanager.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, PARAMETER, TYPE_USE })
@Documented
@Constraint(validatedBy = IpAddressValidator.class)
public @interface IpAddress {

  String message() default "{javax.validation.constraints.IpAddress.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
