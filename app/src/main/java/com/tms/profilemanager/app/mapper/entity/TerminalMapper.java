package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.config.MultimerchantProperties;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.integration.rkl.dto.RKLResponseDto;
import com.tms.profilemanager.app.mapper.entity.dictionary.CardBinRangeMapper;
import com.tms.profilemanager.app.mapper.entity.dictionary.CardCodeMapper;
import com.tms.profilemanager.app.service.CardBinRangeService;
import com.tms.profilemanager.app.service.CardCodeService;
import com.tms.profilemanager.app.service.LicenseService;
import com.tms.profilemanager.app.service.ModificationAcquirerService;
import com.tms.profilemanager.app.service.ModificationTerminalService;
import com.tms.profilemanager.app.service.PaymentScreenLogoService;
import com.tms.profilemanager.app.service.ReceiptTemplateService;
import com.tms.profilemanager.app.service.TerminalService;
import com.tms.profilemanager.app.service.VendorModelService;
import com.tms.profilemanager.dto.TerminalDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.response.TerminalEncryptedKeysResponseDto;
import com.tms.profilemanager.dto.view.DictionaryView;
import com.tms.profilemanager.dto.view.TerminalView;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring", uses = {TerminalGroupMapper.class, TerminalAcquirerParamsMapper.class, MerchantMapper.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class TerminalMapper implements EntityMapper<Terminal, TerminalDto> {

    @Autowired
    TerminalAcquirerParamsMapper terminalAcquirerParamsMapper;
    @Autowired
    LicenseService licenseService;
    @Autowired
    TerminalService terminalService;
    @Autowired
    VendorModelService vendorModelService;
    @Autowired
    CountryMapper countryMapper;
    @Autowired
    PaymentTypeMapper paymentTypeMapper;
    @Autowired
    CardCodeService cardCodeService;
    @Autowired
    CardCodeMapper cardCodeMapper;
    @Autowired
    CardBinRangeService cardBinRangeService;
    @Autowired
    CardBinRangeMapper cardBinRangeMapper;
    @Autowired
    ModificationAcquirerService modificationAcquirerService;
    @Autowired
    ModificationTerminalService modificationTerminalService;
    @Autowired
    ReceiptTemplateMapper receiptTemplateMapper;
    @Autowired
    ReceiptTemplateService receiptTemplateService;
    @Autowired
    PaymentScreenLogoService paymentScreenLogoService;
    @Autowired
    PaymentScreenLogoMapper paymentScreenLogoMapper;
    @Autowired
    MultimerchantProperties multimerchantProperties;

    @Override
    @Mapping(target = TerminalDto.Fields.terminalAcquirerParams, ignore = true)
    @Mapping(target = TerminalDto.Fields.model, ignore = true)
    @Mapping(target = TerminalDto.Fields.country, ignore = true)
    public abstract TerminalDto toDto(Terminal entity);

    @AfterMapping
    protected void afterToDto(@MappingTarget TerminalDto.TerminalDtoBuilder<?, ?> dto, Terminal entity) {
        dto.terminalAcquirerParams(entity.getTerminalAcquirerParams().stream()
                .sorted(Comparator.comparing(TerminalAcquirerParams::getParamsOrder))
                .map(terminalAcquirerParamsMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        vendorModelService.getByIdUsingCacheWithoutAccessControl(entity.getVendorModelId())
                .ifPresent(vendorModel -> {
                    dto.model(vendorModel.getName());
                    dto.vendor(vendorModel.getVendor().getName());
                });
        dto.country(countryMapper.toCountryDto(entity.getCountry()));
    }

    protected TerminalType.Dto toDto(TerminalType type) {
        return TerminalType.Dto.of(type);
    }

    @Mapping(target = TerminalDto.Fields.terminalAcquirerParams, ignore = true)
    @Mapping(target = TerminalDto.Fields.model, ignore = true)
    @Mapping(target = TerminalDto.Fields.country, ignore = true)
    @Mapping(target = TerminalDto.Fields.configUpdatedDate, ignore = true)
    public abstract TerminalView toView(Terminal entity);

    @AfterMapping
    protected void afterToView(@MappingTarget TerminalView.TerminalViewBuilder<?, ?> dto, Terminal entity) {
        dto.paymentTypes(paymentTypeMapper.paramsToPaymentTypeDto(entity.getTerminalAcquirerParams()));
        Set<TerminalAcquirerParams> params = entity.getTerminalAcquirerParams();
        if (entity.getSoftwareVersion() == null ||
                multimerchantProperties.getDisabledSoftwares().contains(entity.getSoftwareName())) {
            params = new LinkedHashSet<>(params.stream()
                    .collect(Collectors.toMap(
                            TerminalAcquirerParams::getAcquirerId,
                            Function.identity(),
                            (a, b) -> a.getParamsOrder() <= b.getParamsOrder() ? a : b,
                            LinkedHashMap::new
                    )).values()
            );
        }
        dto.acquirers(params.stream()
                .map(terminalAcquirerParams -> terminalAcquirerParamsMapper.toView(terminalAcquirerParams))
                .collect(toSet()));
        vendorModelService.getByIdUsingCacheWithoutAccessControl(entity.getVendorModelId())
                .ifPresent(vendorModel -> {
                    dto.model(vendorModel.getName());
                    dto.vendor(vendorModel.getVendor().getName());
                });
        licenseService.findBySn(entity.getSerialNumber()).ifPresent(license -> dto.license(license.getLicenseCode()));
        dto.country(countryMapper.toCountryDto(entity.getCountry()));
        dto.dictionaries(new DictionaryView(
                cardCodeMapper.toDtoList(cardCodeService.getCardCodesUsingCacheWithoutAccessControl()),
                cardBinRangeMapper.toDtoList(cardBinRangeService.getCardBinRangesUsingCacheWithoutAccessControl()))
        );
        dto.receiptTemplate(receiptTemplateMapper.toDto(
                receiptTemplateService.getReceiptTemplateByMerchantId(entity.getMerchantId())));
        if (entity.getGroup() != null) {
            dto.paymentScreenLogos(paymentScreenLogoMapper.toDtoList(
                    paymentScreenLogoService.findAllLogoByGroup(entity.getGroup()).stream().toList()));
        }
    }

    @Transactional
    public TerminalView getConfig(Terminal terminal) {
        TerminalView terminalView = getTerminalViewWithModification(terminal);
        terminalService.setConfigUpdatedDate(terminal);
        return terminalView;
    }

    @Transactional(readOnly = true)
    public TerminalView getTerminalViewWithModification(Terminal terminal) {

        TerminalView terminalView = toView(terminal);

        if (terminal.getGroup() == null) {
            return terminalView;
        }

        // todo добавить кэш
        List<Long> groupIds = terminal.getGroup().getParentPath() != null
                ? Arrays.stream(terminal.getGroup().getParentPath().split("\\.")).map(Long::parseLong).toList()
                : Collections.emptyList();
        List<Long> groupAndParentIds = new ArrayList<>(groupIds);
        groupAndParentIds.add(terminal.getGroup().getId());
        TerminalView terminalViewAfterTerminalModification = modificationTerminalService.getTerminalViewWithModification(terminalView, groupAndParentIds);
        return modificationAcquirerService.getAcquirerWithModification(terminalViewAfterTerminalModification, groupAndParentIds);
    }

    public abstract TerminalEncryptedKeysResponseDto toDtoForTerminal(RKLResponseDto dto);

}
