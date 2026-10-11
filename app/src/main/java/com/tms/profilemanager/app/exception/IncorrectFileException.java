package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;

public class IncorrectFileException extends RuntimeException{

    @Override
    public String getMessage() {
        return MessageSourceUtils.getMessageOrElseCode("validation.incorrect_file");
    }

}
