package com.tms.profilemanager.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class HostValidator implements ConstraintValidator<Host, String> {

    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "^(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$"
    );

    private static final Pattern IP_PATTERN = Pattern.compile(
            "^(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return DOMAIN_PATTERN.matcher(value).matches() || IP_PATTERN.matcher(value).matches();
    }
}

