package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.dto.create.HumoSettingCreateDto;
import com.tms.profilemanager.dto.patch.HumoSettingPatchDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class HumoSettingProvider extends EntityProvider<HumoSetting, HumoSettingCreateDto, HumoSettingPatchDto> {

    @Override
    protected Class<HumoSetting> getEntityClass() {
        return HumoSetting.class;
    }

}
