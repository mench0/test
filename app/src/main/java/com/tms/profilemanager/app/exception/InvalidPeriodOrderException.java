package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvalidPeriodOrderException extends RuntimeException {

    private final OffsetDateTime validFrom;
    private final OffsetDateTime validTo;

    public InvalidPeriodOrderException(OffsetDateTime validFrom, OffsetDateTime validTo) {
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    @Override
    public String getMessage() {
        List<Object> args = new ArrayList<>();
        args.add(validFrom);
        args.add(validTo);
        return MessageSourceUtils.getMessageOrElseCode("validation.invalid_period_order", args);
    }
}

