package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.dto.RoleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class RoleMapper implements EntityMapper<Role, RoleDto> {
}
