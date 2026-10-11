package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.DRL;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.ContactlessLimitService;
import com.tms.profilemanager.dto.create.DRLCreateDto;
import com.tms.profilemanager.dto.patch.DRLPatchDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class DRLProvider extends EntityProvider<DRL, DRLCreateDto, DRLPatchDto> {

    @Autowired @Lazy ContactlessLimitService contactlessLimitService;

    @AfterMapping
    protected void afterInitializeEntity(DRLCreateDto source, @MappingTarget DRL target) {
        if (source.getContactlessLimitId() != null) {
            target.setContactlessLimit(contactlessLimitService.getByIdOrElseThrow(source.getContactlessLimitId()));
        }
    }

    @AfterMapping
    protected void afterPatchEntity(DRLPatchDto source, @MappingTarget DRL target) {
        if (source.getContactlessLimitId() != null) {
            target.setContactlessLimit(source.getContactlessLimitId().map(contactlessLimitService::getByIdOrElseThrow).orElse(null));
        }
    }

    @Override
    protected Class<DRL> getEntityClass() {
        return DRL.class;
    }
}
