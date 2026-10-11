package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.EMVApp;
import com.tms.profilemanager.dto.EMVAppDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class EMVAppMapper implements EntityMapper<EMVApp, EMVAppDto> {
}
