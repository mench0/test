package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.SystemUser;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.RoleService;
import com.tms.profilemanager.dto.create.SystemUserCreateDto;
import com.tms.profilemanager.dto.patch.SystemUserPatchDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class SystemUserProvider extends EntityProvider<SystemUser, SystemUserCreateDto, SystemUserPatchDto> {

    @Autowired @Lazy RoleService roleService;
    @Autowired PasswordEncoder passwordEncoder;

    @AfterMapping
    protected void afterInitializeEntity(SystemUserCreateDto source, @MappingTarget SystemUser target) {
        target.setRole(roleService.getByIdOrElseThrow(source.getRoleId()));
        if (StringUtils.isBlank(source.getPassword())) {
            addValidationError(SystemUserCreateDto.Fields.password, "validation.system-user.password.empty");
        } else {
            target.setPasswordHash(passwordEncoder.encode(source.getPassword()));
        }
    }

    @AfterMapping
    protected void afterPatchEntity(SystemUserPatchDto source, @MappingTarget SystemUser target) {
        if (source.getRoleId() != null) {
            target.setRole(source.getRoleId().map(roleService::getByIdOrElseThrow).orElse(null));
        }
    }

    @Override
    protected Class<SystemUser> getEntityClass() {
        return SystemUser.class;
    }
}
