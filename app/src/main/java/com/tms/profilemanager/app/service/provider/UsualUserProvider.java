package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.UsualUser;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.RoleService;
import com.tms.profilemanager.dto.create.UsualUserCreateDto;
import com.tms.profilemanager.dto.patch.UsualUserPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class UsualUserProvider extends EntityProvider<UsualUser, UsualUserCreateDto, UsualUserPatchDto> {

    @Autowired @Lazy RoleService roleService;
    @Autowired PasswordEncoder passwordEncoder;

    @AfterMapping
    protected void afterInitializeEntity(UsualUserCreateDto source, @MappingTarget UsualUser target) {
        target.setRole(roleService.getByIdOrElseThrow(source.getRoleId()));
        target.setPasswordHash(passwordEncoder.encode(source.getPassword()));
    }

    @AfterMapping
    protected void afterPatchEntity(UsualUserPatchDto source, @MappingTarget UsualUser target) {
        if (source.getRoleId() != null) {
            target.setRole(source.getRoleId().map(roleService::getByIdOrElseThrow).orElse(null));
        }
    }

    @Override
    protected Class<UsualUser> getEntityClass() {
        return UsualUser.class;
    }
}
