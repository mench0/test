package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.CardRangeService;
import com.tms.profilemanager.app.service.PaymentSystemService;
import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.AcquirerDto.AcquirerDtoBuilder;
import com.tms.profilemanager.dto.AcquirerDto.AcquirerPaymentSystemDto;
import com.tms.profilemanager.dto.CurrencyDto;
import com.tms.profilemanager.dto.enums.IsoProtocol;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.view.AcquirerView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class AcquirerMapper implements EntityMapper<Acquirer, AcquirerDto> {

    @Value("${integration.rkl.add-url-to-device-view}")
    private boolean addUrlToDeviceView;

    @Autowired
    PaymentSystemService paymentSystemService;
    @Autowired
    PaymentSystemMapper paymentSystemMapper;
    @Autowired
    CardRangeMapper cardRangeMapper;
    @Autowired
    CardRangeService cardRangeService;

    @Override
    @Mapping(target = "isoProtocol", ignore = true)
    @Mapping(target = "availableCurrencies", ignore = true)
    @Mapping(target = "availableOperations", ignore = true)
    public abstract AcquirerDto toDto(Acquirer entity);

    @AfterMapping
    protected void afterToDto(@MappingTarget AcquirerDtoBuilder<?, ?> dto, Acquirer entity) {
        dto.isoProtocol(IsoProtocol.Dto.of(entity.getIsoProtocol()));
        if (entity.getAvailableCurrencies() != null) {
            dto.availableCurrencies(entity.getAvailableCurrencies().stream().map(CurrencyDto::of).collect(toSet()));
        }
        if (entity.getOrderedAvailableOperations() != null) {
            dto.availableOperations(entity.getOrderedAvailableOperations());
        }
        if (entity.getPaymentSystemIds() != null) {
            dto.paymentSystemsShortInfo(entity.getPaymentSystemIds().stream()
                    .map(id -> paymentSystemService.getByIdUsingCacheWithoutAccessControl(id).orElseThrow())
                    .filter(paymentSystem -> !paymentSystem.isArchived())
                    .map(this::toDto)
                    .collect(toSet()));
        }
    }

    protected abstract AcquirerPaymentSystemDto toDto(PaymentSystem paymentSystem);

    @Mapping(target = "availableOperations", ignore = true)
    @Mapping(target = "rklUrl", ignore = true)
    public abstract AcquirerView toView(Acquirer entity, @Context TerminalType terminalType);

    @AfterMapping
    protected void afterToView(@MappingTarget AcquirerView.AcquirerViewBuilder<?, ?> dto, Acquirer entity, @Context TerminalType terminalType) {
        if (addUrlToDeviceView && entity.getRklUrl() != null) {
            dto.rklUrl(toDto(entity.getRklUrl()));
        }
        dto.paymentSystems(entity.getPaymentSystemIds().stream()
                .map(id -> paymentSystemService.getByIdUsingCacheWithoutAccessControl(id).orElseThrow())
                .filter(paymentSystem -> !paymentSystem.isArchived())
                .map(paymentSystem -> paymentSystemMapper.toView(paymentSystem, terminalType))
                .collect(toSet()));
        dto.cardRanges(cardRangeService.getByAcquirerIdUsingCacheWithoutAccessControl(entity.getId()).stream()
                .filter(cardRange -> !cardRange.isArchived())
                .map(cardRangeMapper::toViewDto)
                .collect(toSet()));
        dto.availableOperations(entity.getOrderedAvailableOperations());
    }

    protected abstract AcquirerDto.RklUrlDto toDto(Acquirer.RklUrl rklHost);
}
