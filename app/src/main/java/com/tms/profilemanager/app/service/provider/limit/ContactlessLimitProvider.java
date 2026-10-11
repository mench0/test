package com.tms.profilemanager.app.service.provider.limit;

import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.DRLService;
import com.tms.profilemanager.app.service.provider.EntityProvider;
import com.tms.profilemanager.dto.create.limit.ContactlessLimitCreateDto;
import com.tms.profilemanager.dto.patch.ContactlessLimitPatchDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;

import static java.util.stream.Collectors.toList;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class ContactlessLimitProvider extends
        EntityProvider<ContactlessLimit, ContactlessLimitCreateDto, ContactlessLimitPatchDto> {

    @Autowired @Lazy DRLService drlService;

    @AfterMapping
    protected void afterInitializeEntity(ContactlessLimitCreateDto source, @MappingTarget ContactlessLimit target) {
        if (source.getDrlIdList() != null) {
            target.setDrlList(source.getDrlIdList().stream().map(drlService::getByIdOrElseThrow).collect(toList()));
            target.getDrlList().forEach(it -> it.setContactlessLimit(target));
        }
    }

    @AfterMapping
    protected void afterPatchEntity(ContactlessLimitPatchDto source, @MappingTarget ContactlessLimit target) {
        if (source.getDrlIdList() != null) {
            target.getDrlList().forEach(it -> it.setContactlessLimit(null));
            target.getDrlList().clear();
            source.getDrlIdList().orElse(new ArrayList<>()).stream()
                    .map(drlService::getByIdOrElseThrow)
                    .forEach(it -> target.getDrlList().add(it));
            target.getDrlList().forEach(it -> it.setContactlessLimit(target));
        }
    }

    @Override
    protected Class<ContactlessLimit> getEntityClass() {
        return ContactlessLimit.class;
    }
}
