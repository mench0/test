package com.tms.profilemanager.app.service.provider.limit;

import com.tms.profilemanager.app.entity.ContactLimit;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.provider.EntityProvider;
import com.tms.profilemanager.dto.create.limit.ContactLimitCreateDto;
import com.tms.profilemanager.dto.patch.ContactLimitPatchDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class ContactLimitProvider extends EntityProvider<ContactLimit, ContactLimitCreateDto, ContactLimitPatchDto> {

    @Override
    protected Class<ContactLimit> getEntityClass() {
        return ContactLimit.class;
    }
}
