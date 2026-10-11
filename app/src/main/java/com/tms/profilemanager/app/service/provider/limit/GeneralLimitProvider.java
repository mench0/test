package com.tms.profilemanager.app.service.provider.limit;

import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.ContactLimitService;
import com.tms.profilemanager.app.service.ContactlessLimitService;
import com.tms.profilemanager.app.service.provider.EntityProvider;
import com.tms.profilemanager.dto.create.limit.GeneralLimitCreateDto;
import com.tms.profilemanager.dto.patch.GeneralLimitPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class GeneralLimitProvider extends EntityProvider<GeneralLimit, GeneralLimitCreateDto, GeneralLimitPatchDto> {

    @Autowired
    @Lazy
    ContactlessLimitService contactlessLimitService;
    @Autowired
    @Lazy
    ContactLimitService contactLimitService;

    @AfterMapping
    protected void afterInitializeEntity(GeneralLimitCreateDto source, @MappingTarget GeneralLimit target) {
        if (source.getContactlessLimitId() != null) {
            target.setContactlessLimit(contactlessLimitService.getByIdOrElseThrow(source.getContactlessLimitId()));
        }
        if (source.getContactLimitId() != null) {
            target.setContactLimit(contactLimitService.getByIdOrElseThrow(source.getContactLimitId()));
        }
    }

    @AfterMapping
    protected void afterPatchEntity(GeneralLimitPatchDto source, @MappingTarget GeneralLimit target) {
        if (source.getContactlessLimitId() != null) {
            target.setContactlessLimit(source.getContactlessLimitId().map(contactlessLimitService::getByIdOrElseThrow).orElse(null));
        }
        if (source.getContactLimitId() != null) {
            target.setContactLimit(source.getContactLimitId().map(contactLimitService::getByIdOrElseThrow).orElse(null));
        }
    }

    @Override
    protected Class<GeneralLimit> getEntityClass() {
        return GeneralLimit.class;
    }

}
