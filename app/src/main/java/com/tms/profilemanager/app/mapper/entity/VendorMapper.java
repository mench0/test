package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.Vendor;
import com.tms.profilemanager.dto.VendorDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class VendorMapper implements EntityMapper<Vendor, VendorDto> {

}
