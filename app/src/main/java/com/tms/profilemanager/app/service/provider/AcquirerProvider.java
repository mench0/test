package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.AcquirerOperation;
import com.tms.profilemanager.app.mapper.entity.AcquirerMapper;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.PaymentSystemService;
import com.tms.profilemanager.app.service.TerminalAcquirerParamsService;
import com.tms.profilemanager.dto.create.AcquirerCreateDto;
import com.tms.profilemanager.dto.patch.AcquirerPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;

import java.util.Iterator;
import java.util.stream.IntStream;

import static java.lang.Boolean.TRUE;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class AcquirerProvider extends EntityProvider<Acquirer, AcquirerCreateDto, AcquirerPatchDto> {

    @Value("${integration.rkl.custom-url-required}")
    private boolean customUrlRequired;

    @Autowired @Lazy PaymentSystemService paymentSystemService;
    @Autowired @Lazy TerminalAcquirerParamsService terminalAcquirerParamsService;
    @Autowired @Lazy AcquirerMapper acquirerMapper;

    @Override
    @Mapping(target = "paymentSystemIds", ignore = true)
    @Mapping(target = "availableOperations", ignore = true)
    public abstract void initializeEntity(AcquirerCreateDto source, @MappingTarget Acquirer target);

    @AfterMapping
    protected void afterInitializeEntity(AcquirerCreateDto source, @MappingTarget Acquirer target) {
        checkRkl(target);

        if (source.getAvailableOperations() != null) {
            IntStream.range(0, source.getAvailableOperations().size()).forEach(
                    i -> target.getAvailableOperations().add(
                            AcquirerOperation.builder()
                                    .acquirer(target)
                                    .operation(source.getAvailableOperations().get(i))
                                    .operationOrder(i + 1)
                                    .build()
                    ));
        }
        if (target.getOrderedAvailableOperations().isEmpty()) {
            addValidationError(AcquirerPatchDto.Fields.availableOperations, "validation.acquirer.available-operations.empty");
        }
        source.getPaymentSystemsIds().forEach(paymentSystemService::existsByIdOrElseThrow);
        target.setPaymentSystemIds(source.getPaymentSystemsIds());
    }

    @Override
    @Mappings({
        @Mapping(target = "paymentSystemIds", ignore = true),
        @Mapping(target = "availableOperations", ignore = true),
        @Mapping(target = "availableCurrencies", ignore = true),
    })
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(AcquirerPatchDto patchDto, @MappingTarget Acquirer entity);

    @AfterMapping
    protected void afterPatchEntity(AcquirerPatchDto source, @MappingTarget Acquirer target) {
        checkRkl(target);

        if (source.getPaymentSystemsIds() != null) {
            source.getPaymentSystemsIds().forEachValue(paymentSystemService::existsByIdOrElseThrow);
            patchSet(source.getPaymentSystemsIds(), target.getPaymentSystemIds());
            if (target.getPaymentSystemIds().isEmpty()) {
                addValidationError(AcquirerPatchDto.Fields.paymentSystemsIds, "validation.acquirer.payment-systems.empty");
            }
        }
        if (source.getAvailableOperations() != null) {
            Iterator<AcquirerOperation> iterator = target.getAvailableOperations().iterator();
            while (iterator.hasNext()) {
                AcquirerOperation acquirerOperation = iterator.next();
                if (!source.getAvailableOperations().contains(acquirerOperation.getOperation())) {
                    iterator.remove();
                    terminalAcquirerParamsService.removeTerminalAcquirerParamsAvailableOperations(target.getId(), acquirerOperation.getOperation());
                }
            }
            IntStream.range(0, source.getAvailableOperations().size())
                    .forEach(i -> target.getAvailableOperation(source.getAvailableOperations().get(i))
                            .ifPresentOrElse(
                                    operation -> operation.setOperationOrder(i + 1),
                                    () -> {
                                        target.getAvailableOperations().add(
                                                AcquirerOperation.builder()
                                                        .acquirer(target)
                                                        .operation(source.getAvailableOperations().get(i))
                                                        .operationOrder(i + 1)
                                                        .build()
                                        );
                                        terminalAcquirerParamsService.addTerminalAcquirerParamsAvailableOperations(target.getId(), source.getAvailableOperations().get(i));
                                    }
                            )
                    );
        }
        if (target.getOrderedAvailableOperations().isEmpty()) {
            addValidationError(AcquirerPatchDto.Fields.availableOperations, "validation.acquirer.available-operations.empty");
        }
        if (source.getAvailableCurrencies() != null) {
            patchSet(source.getAvailableCurrencies(), target.getAvailableCurrencies());
            if (target.getAvailableCurrencies().isEmpty()) {
                addValidationError(AcquirerPatchDto.Fields.availableCurrencies, "validation.acquirer.available-currencies.empty");
            }
        }
    }

    private void checkRkl(Acquirer target) {
        if (customUrlRequired) {
            if(target.getRklUrl() == null) {
                addValidationError("rklUrl", "validation.acquirer.rkl.NotNull");
            }
            if(target.getRklUrl() != null
                    && TRUE.equals(target.getRklUrl().getRklEnable())
                    && target.getRklUrl().getRklHost() == null) {
                addValidationError("rklHost", "validation.acquirer.rkl.rklHost.NotNull");
            }
        }
    }

    @Override
    protected Class<Acquirer> getEntityClass() {
        return Acquirer.class;
    }
}
