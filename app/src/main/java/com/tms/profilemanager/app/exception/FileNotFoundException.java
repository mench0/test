package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;

public class FileNotFoundException extends RuntimeException {

    @Override
    public String getMessage() {
        return MessageSourceUtils.getMessageOrElseCode("validation.file_not_found");
    }
}
