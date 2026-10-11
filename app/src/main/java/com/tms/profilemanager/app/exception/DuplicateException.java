package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;

import java.util.ArrayList;
import java.util.List;

public class DuplicateException extends RuntimeException {

    String entity;

    public DuplicateException(String entity) {
        this.entity = entity;
    }

    @Override
    public String getMessage() {
        List<Object> args = new ArrayList<>();
        args.add(entity);
        return MessageSourceUtils.getMessageOrElseCode("validation.duplicate", args);
    }
}
