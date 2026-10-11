package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.config.MultimerchantProperties;
import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.exception.DuplicateException;
import com.tms.profilemanager.app.integration.humo.config.HumoProperties;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.*;
import com.tms.profilemanager.dto.create.TerminalCreateDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto;
import com.tms.profilemanager.dto.patch.TerminalPatchDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class TerminalProvider extends EntityProvider<Terminal, TerminalCreateDto, TerminalPatchDto> {

    @Autowired
    @Lazy
    AcquirerService acquirerService;
    @Autowired
    @Lazy
    MerchantService merchantService;
    @Autowired
    @Lazy
    VendorModelService vendorModelService;
    @Autowired
    @Lazy
    TerminalGroupService terminalGroupService;
    @Autowired
    @Lazy
    TerminalAcquirerParamsProvider terminalAcquirerParamsProvider;
    @Autowired
    @Lazy
    CountryService countryService;
    @Autowired
    @Lazy
    HumoProperties humoProperties;
    @Autowired
    @Lazy
    MultimerchantProperties multimerchantProperties;

    @Override
    @Mapping(target = "terminalAcquirerParams", ignore = true)
    public abstract void initializeEntity(TerminalCreateDto source, @MappingTarget Terminal target);

    @AfterMapping
    protected void afterInitializeEntity(TerminalCreateDto source, @MappingTarget Terminal target) {
        target.setMerchant(merchantService.getByIdOrElseThrow(source.getMerchantId()));
        vendorModelService.existsByIdOrElseThrow(source.getModelId());
        target.setVendorModelId(source.getModelId());
        if (source.getGroupId() != null) {
            target.setGroup(terminalGroupService.getByIdOrElseThrow(source.getGroupId()));
        }
        List<TerminalAcquirerParamsInputDto> params = source.getTerminalAcquirerParams();
        if (isNotEmpty(params)) {
            checkAcquirers(params);
            createAndLinkOrderedParamsToTerminal(target, params);
        }
        if (source.getCountry() != null) {
            target.setCountry(countryService.findCountry(source.getCountry()).getName());
        }
    }

    @Override
    @Mapping(target = "terminalAcquirerParams", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(TerminalPatchDto patchDto, @MappingTarget Terminal entity);

    @AfterMapping
    protected void afterPatchEntity(TerminalPatchDto source, @MappingTarget Terminal target) {
        if (source.getGroupId() != null) {
            target.setGroup(source.getGroupId().map(terminalGroupService::getByIdOrElseThrow).orElse(null));
        }
        if (source.getTerminalAcquirerParams() != null) {
            patchTerminalAcquirerParams(source, target);
        }
        if (source.getModelId() != null) {
            vendorModelService.existsByIdOrElseThrow(source.getModelId().get());
            target.setVendorModelId(source.getModelId().get());
        }
    }

    public void patchTerminalAcquirerParams(TerminalPatchDto source, Terminal target) {
        List<TerminalAcquirerParamsInputDto> values = source.getTerminalAcquirerParams().getValues();
        checkAcquirers(values);
        switch (source.getTerminalAcquirerParams().getPatchType()) {
            case REPLACE -> {
                IntStream.range(0, values.size()).forEach(i -> {
                    var dto = values.get(i);

                    target.getTerminalAcquirerParams(dto.getAcquirerId(), dto.getIsoTerminalId(), dto.getIsoMerchantId()).ifPresentOrElse(
                            params -> {
                                terminalAcquirerParamsProvider.patch(dto, params);
                                params.setParamsOrder(i + 1);
                            },
                            () -> {
                                var params = createParamsAndLinkWithTerminal(dto, target, i + 1);
                                target.getTerminalAcquirerParams().add(params);
                            }
                    );
                });


                Set<AcquirerKey> patchingAcquirer = source.getTerminalAcquirerParams().getValues().stream()
                        .map(paramsInputDto -> new AcquirerKey(paramsInputDto.getAcquirerId(), paramsInputDto.getIsoTerminalId(), paramsInputDto.getIsoMerchantId()))
                        .collect(Collectors.toSet());

                target.getTerminalAcquirerParams().removeIf(it ->
                        !patchingAcquirer.contains(new AcquirerKey(it.getAcquirerId(), it.getIsoTerminalId(), it.getIsoMerchantId())));
            }
            case ADD -> createAndLinkOrderedParamsToTerminal(target, values);
            case REMOVE -> source.getTerminalAcquirerParams().getValues().forEach(paramsInputDto ->
                    target.getTerminalAcquirerParams().removeIf(it -> it.getAcquirerId().equals(paramsInputDto.getAcquirerId())));
        }
    }

    private void createAndLinkOrderedParamsToTerminal(Terminal target, List<TerminalAcquirerParamsInputDto> params) {
        IntStream.range(0, params.size())
                .forEach(i ->
                        target.getTerminalAcquirerParams().add(createParamsAndLinkWithTerminal(params.get(i), target, i + 1)));

    }

    private TerminalAcquirerParams createParamsAndLinkWithTerminal(TerminalAcquirerParamsInputDto inputDto, Terminal terminal, int i) {
        var params = terminalAcquirerParamsProvider.create(inputDto);
        params.setTerminal(terminal);
        params.setParamsOrder(i);
        return params;
    }

    private void checkAcquirers(List<TerminalAcquirerParamsInputDto> params) {
        Set<Long> acquirerIds = params.stream()
                .map(TerminalAcquirerParamsInputDto::getAcquirerId)
                .collect(Collectors.toSet());

        List<String> duplicateAcquirers = acquirerService.getByIds(acquirerIds).stream()
                .filter(acquirer -> multimerchantProperties.getDisabledAcquirers().contains(acquirer.getName()))
                .filter(acquirer -> params.stream()
                        .filter(p -> acquirer.getId().equals(p.getAcquirerId()))
                        .count() > 1
                )
                .map(Acquirer::getName)
                .toList();

        if (!duplicateAcquirers.isEmpty()) {
            throw new DuplicateException(String.join(", ", duplicateAcquirers));
        }

        Set<AcquirerKey> keys = params.stream()
                .map(AcquirerKey::from)
                .collect(Collectors.toSet());

        if (keys.size() != params.size()) {
            throw new DuplicateException("AcquirerId + isoTerminalId + isoMerchantId");
        }
    }

    @Override
    protected Class<Terminal> getEntityClass() {
        return Terminal.class;
    }

    public record AcquirerKey(Long acquirerId, String isoTerminalId, String isoMerchantId) {
        public static AcquirerKey from(TerminalAcquirerParamsInputDto p) {
            return new AcquirerKey(p.getAcquirerId(), p.getIsoTerminalId(), p.getIsoMerchantId());
        }
    }
}
