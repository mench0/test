package com.tms.profilemanager.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponseDto {

    @Schema(description = "Код ошибки")
    String errorCode;

    @Schema(description = "Описание ошибки")
    String errorMessage;

    public interface CommonErrorCodes {
        String SERVER_ERROR = "server.error";
        String SERVICE_UNAVAILABLE = "service.unavailable";
        String VALIDATION_ERROR = "validation.error";
        String ACCESS_DENIED = "access.denied";
        String AUTHENTICATION_FAILED = "authentication.failed";
        String ROLE_EDIT_FORBIDDEN = "role.edit.forbidden";
    }
}
