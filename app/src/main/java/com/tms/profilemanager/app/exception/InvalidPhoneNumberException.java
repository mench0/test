package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;

import java.util.ArrayList;
import java.util.List;

public class InvalidPhoneNumberException extends RuntimeException {

    String phone;

    public InvalidPhoneNumberException(String phone) {
        this.phone = phone;
    }

    @Override
    public String getMessage() {
        List<Object> args = new ArrayList<>();
        args.add(phone);
        return MessageSourceUtils.getMessageOrElseCode("validation.incorrect_phone_number", args);
    }


}
