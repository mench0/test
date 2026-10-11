package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.dto.create.VendorCreateDto.VendorModelInputDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class VendorModelProvider {

    protected abstract void initializeEntity(VendorModelInputDto inputDto, @MappingTarget VendorModel entity);

    public abstract void patch(VendorModelInputDto inputDto, @MappingTarget VendorModel entity);

}
