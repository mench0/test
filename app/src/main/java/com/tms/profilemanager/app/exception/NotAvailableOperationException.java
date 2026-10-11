package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotAvailableOperationException extends RuntimeException {

    Set<AcquirerOperationType> acquirerOperationTypes;

    @Override
    public String getMessage() {
        String acquirerOperationsStr = acquirerOperationTypes.stream()
                .map(AcquirerOperationType::name)
                .map(MessageSourceUtils::getMessageOrElseCode)
                .collect(Collectors.joining(", "));
        return MessageSourceUtils.getMessageOrElseCode("not_available_acquirer_operation", List.of(acquirerOperationsStr));
    }
}
