package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.dto.VendorModelDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class VendorModelMapper implements EntityMapper<VendorModel, VendorModelDto> {

}