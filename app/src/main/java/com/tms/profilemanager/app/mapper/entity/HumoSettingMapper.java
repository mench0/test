package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.app.service.VendorModelService;
import com.tms.profilemanager.dto.HumoSettingDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class HumoSettingMapper implements EntityMapper<HumoSetting, HumoSettingDto> {

    @Autowired
    AcquirerService acquirerService;
    @Autowired
    VendorModelService vendorModelService;

    @Override
    public HumoSettingDto toDto(HumoSetting entity) {
        return toDtoList(List.of(entity)).get(0);
    }

    @Override
    public List<HumoSettingDto> toDtoList(List<HumoSetting> humoSettings) {
        Set<Long> acquirerIds = humoSettings.stream().map(HumoSetting::getAcquirerId).collect(toSet());
        Set<Long> modelIds = humoSettings.stream().map(HumoSetting::getModelId).collect(toSet());
        Map<Long, String> acquirersName = acquirerService.getAcquirersName(acquirerIds);
        Map<Long, String> modelsName = vendorModelService.getModelsName(modelIds);
        return humoSettings.stream()
                .map(humoSetting ->
                        toHumoSettingDto(humoSetting, new HumoSettingMappingContext(
                                modelsName.get(humoSetting.getModelId()),
                                acquirersName.get(humoSetting.getAcquirerId())))
                )
                .collect(Collectors.toList());
    }

    protected abstract HumoSettingDto toHumoSettingDto(HumoSetting humoSetting, @Context HumoSettingMappingContext modelAndAcquirer);

    @AfterMapping
    protected void afterToHumoSettingDto(@MappingTarget HumoSettingDto.HumoSettingDtoBuilder<?, ?> dto, @Context HumoSettingMappingContext modelAndAcquirer) {
        dto.model(modelAndAcquirer.model());
        dto.acquirer(modelAndAcquirer.acquirer());
    }

    protected record HumoSettingMappingContext(String model, String acquirer) {
    }
}
