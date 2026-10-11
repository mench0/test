package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.License;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.dto.create.LicenseCreateDto;
import com.tms.profilemanager.dto.patch.LicensePatchDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class LicenseProvider extends EntityProvider<License, LicenseCreateDto, LicensePatchDto> {

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(LicensePatchDto patchDto, @MappingTarget License entity);

    @Override
    protected Class<License> getEntityClass() {
        return License.class;
    }

}
