package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.License;
import com.tms.profilemanager.dto.LicenseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class LicenseMapper implements EntityMapper<License, LicenseDto> {
}
