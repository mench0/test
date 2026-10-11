package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.PaymentType;
import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.service.PaymentTypeService;
import com.tms.profilemanager.dto.PaymentTypeDto;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tms.profilemanager.dto.enums.AcquirerOperationType.PAYMENT;

@Mapper(componentModel = "spring")
public abstract class PaymentTypeMapper implements EntityMapper<PaymentType, PaymentTypeDto> {

    @Autowired
    @Lazy
    PaymentTypeService paymentTypeService;

    public List<PaymentTypeDto> paramsToPaymentTypeDto(Set<TerminalAcquirerParams> terminalAcquirerParams) {
        if (terminalAcquirerParams.stream().anyMatch(params -> params.getAvailableOperations().stream()
                .anyMatch(acquirerOperation -> acquirerOperation.getOperation().equals(PAYMENT)))) {
            Set<String> paymentTypes = new HashSet<>();
            terminalAcquirerParams.forEach(params -> paymentTypes.addAll(params.getParamsPaymentTypes()));
            return toDtoList(paymentTypeService.getAllPaymentTypesUsingCacheWithoutAccessControl()
                    .stream()
                    .filter(it -> paymentTypes.contains(it.getPaymentValue()))
                    .toList());
        }
        return Collections.emptyList();
    }

    public List<PaymentTypeDto> paymentTypeToDto(List<String> paymentTypes) {
        return toDtoList(paymentTypeService.getAllPaymentTypesUsingCacheWithoutAccessControl()
                .stream()
                .filter(it -> paymentTypes.contains(it.getPaymentValue()))
                .toList());
    }
}
