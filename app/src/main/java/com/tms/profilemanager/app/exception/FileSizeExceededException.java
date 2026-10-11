package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;
import org.springframework.util.unit.DataSize;

import java.util.ArrayList;
import java.util.List;

public class FileSizeExceededException  extends RuntimeException{

    DataSize size;

    public FileSizeExceededException(DataSize size) {
        this.size = size;
    }

    @Override
    public String getMessage() {
        List<Object> args = new ArrayList<>();
        args.add(size.toKilobytes());
        return MessageSourceUtils.getMessageOrElseCode("validation.file_size_exceeded", args);
    }
}
