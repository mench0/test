package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.AcquirerOperation;
import com.tms.profilemanager.app.entity.TerminalAcquirerOperation;
import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.entity.PaymentType;
import com.tms.profilemanager.app.exception.NotAvailableOperationException;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.app.service.PaymentTypeService;
import com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tms.profilemanager.dto.enums.AcquirerOperationType.PAYMENT;


@Mapper(componentModel = "spring")
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class TerminalAcquirerParamsProvider {

    @Lazy
    @Autowired
    AcquirerService acquirerService;
    @Lazy
    @Autowired
    PaymentTypeService paymentTypeService;

    @SneakyThrows
    public TerminalAcquirerParams create(TerminalAcquirerParamsInputDto inputDto) {
        TerminalAcquirerParams entity = new TerminalAcquirerParams();
        initializeEntity(inputDto, entity);
        return entity;
    }

    @Mapping(target = "availableOperations", ignore = true)
    @Mapping(target = "paramsPaymentTypes", ignore = true)
    protected abstract void initializeEntity(TerminalAcquirerParamsInputDto inputDto, @MappingTarget TerminalAcquirerParams entity);

    @AfterMapping
    protected void afterInitializeEntity(TerminalAcquirerParamsInputDto inputDto, @MappingTarget TerminalAcquirerParams entity) {
        Acquirer acquirer = acquirerService.getByIdOrElseThrow(inputDto.getAcquirerId());
        if (inputDto.getAvailableOperations() != null && !acquirer.getAvailableOperations().isEmpty()) {
            assertAvailableOperations(inputDto.getAvailableOperations(), acquirer);

            entity.getAvailableOperations().removeIf(
                    operation -> !inputDto.getAvailableOperations().contains(operation.getOperation())
            );

            IntStream.range(0, inputDto.getAvailableOperations().size())
                    .forEach(i -> entity.getAvailableOperation(inputDto.getAvailableOperations().get(i))
                            .ifPresentOrElse(
                                    operation -> operation.setOperationOrder(i + 1),
                                    () -> entity.getAvailableOperations().add(
                                            TerminalAcquirerOperation.builder()
                                                    .terminalAcquirerParams(entity)
                                                    .operation(inputDto.getAvailableOperations().get(i))
                                                    .operationOrder(i + 1)
                                                    .build()
                                    )
                            )
                    );
            paymentTypeMapping(inputDto, entity);
        }
        entity.setAcquirerId(acquirer.getId());
    }

    @Mapping(target = "availableOperations", ignore = true)
    @Mapping(target = "paramsPaymentTypes", ignore = true)
    public abstract void patch(TerminalAcquirerParamsInputDto inputDto, @MappingTarget TerminalAcquirerParams entity);

    private void assertAvailableOperations(List<AcquirerOperationType> operationTypes, Acquirer acquirer) {
        Set<AcquirerOperationType> notAvailableOperationTypes = operationTypes.stream()
                .filter(operationType -> acquirer.getAvailableOperations().stream()
                        .map(AcquirerOperation::getOperation)
                        .noneMatch(operationType::equals))
                .collect(Collectors.toSet());
        if (!notAvailableOperationTypes.isEmpty()) {
            throw new NotAvailableOperationException(notAvailableOperationTypes);
        }
    }

    private void paymentTypeMapping(TerminalAcquirerParamsInputDto inputDto, TerminalAcquirerParams entity) {
        if (entity.getAvailableOperations().stream().anyMatch(acquirerOperation -> acquirerOperation.getOperation().equals(PAYMENT))) {
            entity.setParamsPaymentTypes(paymentTypeService.getAllPaymentTypesUsingCacheWithoutAccessControl()
                    .stream()
                    .map(PaymentType::getPaymentValue)
                    .filter(paymentType -> inputDto.getParamsPaymentTypes().contains(paymentType))
                    .collect(Collectors.toList()));
        } else {
            entity.setParamsPaymentTypes(new ArrayList<>());
        }
    }
}
