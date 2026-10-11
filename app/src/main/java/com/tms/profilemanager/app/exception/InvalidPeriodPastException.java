package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvalidPeriodPastException extends RuntimeException {

    private final OffsetDateTime validTo;
    private final OffsetDateTime now;

    public InvalidPeriodPastException(OffsetDateTime validTo, OffsetDateTime now) {
        this.validTo = validTo;
        this.now = now;
    }

    @Override
    public String getMessage() {
        List<Object> args = new ArrayList<>();
        args.add(validTo);
        args.add(now);
        return MessageSourceUtils.getMessageOrElseCode("validation.invalid_period_past", args);
    }
}

