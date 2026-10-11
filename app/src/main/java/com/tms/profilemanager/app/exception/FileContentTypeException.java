package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;

import java.util.ArrayList;
import java.util.List;

public class FileContentTypeException extends RuntimeException {

    List<String> contentTypes;
    String actualContentType;

    public FileContentTypeException(List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    @Override
    public String getMessage() {
        List<Object> args = new ArrayList<>();
        args.add(contentTypes);
        args.add(actualContentType);
        return MessageSourceUtils.getMessageOrElseCode("validation.file_content_type", args);
    }
}
