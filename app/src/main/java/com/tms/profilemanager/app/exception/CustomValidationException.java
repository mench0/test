package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;
import com.tms.profilemanager.dto.error.ValidationErrorDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomValidationException extends RuntimeException {

    List<ValidationErrorDto> validationErrors = new ArrayList<>();

    public CustomValidationException addValidationError(String field, String errorCode, Object... args) {
        validationErrors.add(ValidationErrorDto.builder()
                .field(field)
                .errorCode(errorCode)
                .errorMessage(MessageSourceUtils.getMessageOrElseCode(errorCode, Arrays.asList(args)))
                .build());
        return this;
    }

    @Override
    public String getMessage() {
        return validationErrors.stream().map(ValidationErrorDto::getErrorMessage).collect(joining("\n"));
    }
}
