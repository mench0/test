package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.dto.error.ErrorResponseDto;
import com.tms.profilemanager.dto.error.ValidationErrorDto;
import com.tms.profilemanager.dto.error.ValidationErrorResponseDto;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.CredentialException;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import static com.tms.profilemanager.app.utils.MessageSourceUtils.getMessageOrElseCode;
import static com.tms.profilemanager.dto.error.ErrorResponseDto.CommonErrorCodes.*;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handle(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ValidationErrorResponseDto> handle(ConstraintViolationException e) {
        log.error("Validation error:", e);
        return ResponseEntity
                .badRequest()
                .body(ValidationErrorResponseDto.builder()
                        .errorCode(VALIDATION_ERROR)
                        .validationErrors(e.getConstraintViolations().stream()
                                .map(it -> ValidationErrorDto.builder()
                                        .field(getFieldName(it.getPropertyPath()))
                                        .invalidValue(it.getInvalidValue())
                                        .errorCode(it.getMessageTemplate().substring(1, it.getMessageTemplate().length() - 1))
                                        .errorMessage(it.getMessage())
                                        .build())
                                .toList())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ValidationErrorResponseDto> handle(CustomValidationException e) {
        log.error("Validation error:", e);
        return ResponseEntity
                .badRequest()
                .body(ValidationErrorResponseDto.builder()
                        .errorCode(VALIDATION_ERROR)
                        .validationErrors(e.getValidationErrors())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException e) {
        log.error("Validation error:", e);
        return ResponseEntity
                .badRequest()
                .body(ErrorResponseDto.builder()
                        .errorCode(VALIDATION_ERROR)
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(EntityNotFoundByFieldException e) {
        log.error("Entity not found error:", e);
        return ResponseEntity
                .badRequest()
                .body(ErrorResponseDto.builder()
                        .errorCode(e.getErrorCode())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(FileNotFoundException e) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponseDto.builder()
                        .errorCode(VALIDATION_ERROR)
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(IncorrectFileException e) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponseDto.builder()
                        .errorCode(VALIDATION_ERROR)
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(VendorModelLinkedWithTerminalException e) {
        log.error("Incorrect field:", e);
        return ResponseEntity
                .badRequest()
                .body(ErrorResponseDto.builder()
                        .errorCode(VALIDATION_ERROR)
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(DuplicateException e) {
        log.error("Duplicate field:", e);
        return ResponseEntity
                .badRequest()
                .body(ErrorResponseDto.builder()
                        .errorCode(VALIDATION_ERROR)
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDto.builder()
                        .errorCode(ACCESS_DENIED)
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(PermissionsNotAvailableException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDto.builder()
                        .errorCode(ACCESS_DENIED)
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(NotAvailableOperationException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDto.builder()
                        .errorCode(ACCESS_DENIED)
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(RoleEditFobiddenException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDto.builder()
                        .errorCode(ROLE_EDIT_FORBIDDEN)
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(CredentialException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDto.builder()
                        .errorCode(AUTHENTICATION_FAILED)
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> handleDatabaseException(DataIntegrityViolationException e) {
        log.error("Database exception:", e);
        if (e.getCause() instanceof  org.hibernate.exception.ConstraintViolationException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponseDto.builder()
                            .errorCode(VALIDATION_ERROR)
                            .errorMessage(getMessageOrElseCode("database.constraint." + exception.getConstraintName()))
                            .build());
        }
        return ResponseEntity
                .internalServerError()
                .body(ErrorResponseDto.builder()
                        .errorCode(SERVER_ERROR)
                        .errorMessage(e.toString())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(CallNotPermittedException e) {
        log.error("Call not permitted:", e);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponseDto.builder()
                        .errorCode(SERVICE_UNAVAILABLE)
                        .errorMessage(e.toString())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handle(Exception e) {
        log.error("Unexpected error:", e);
        return ResponseEntity
                .internalServerError()
                .body(ErrorResponseDto.builder()
                        .errorCode(SERVER_ERROR)
                        .errorMessage(e.toString())
                        .build());
    }

    private String getFieldName(Path path) {
        String name = null;
        for (Path.Node node : path) {
            name = node.getName();
        }
        return name;
    }
}
