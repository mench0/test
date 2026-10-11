package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;

import java.util.ArrayList;
import java.util.List;

public class VendorModelLinkedWithTerminalException extends RuntimeException {

    String model;
    String serialNumber;

    public VendorModelLinkedWithTerminalException(String model, String serialNumber) {
        this.model = model;
        this.serialNumber = serialNumber;
    }

    @Override
    public String getMessage() {
        List<Object> args = new ArrayList<>();
        args.add(model);
        args.add(serialNumber);
        return MessageSourceUtils.getMessageOrElseCode("validation.model.liked_terminal", args);
    }
}
