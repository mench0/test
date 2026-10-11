package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.app.utils.AuthenticationHolder;
import com.tms.profilemanager.app.utils.MessageSourceUtils;
import com.tms.profilemanager.dto.enums.Permission;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class PermissionMapper {

    public Permission.Dto toDto(Permission permission) {
        return Permission.Dto.builder()
                .code(permission.getCode())
                .name(MessageSourceUtils.getMessageOrElseCode(permission.getCode()))
                .build();
    }

    public Set<Permission.RoleDto> toRoleDto(Role role) {
        Set<Permission> userPermissions = AuthenticationHolder.getCurrentUserInfoOrElseThrow().getPermissions();
        Set<Permission.RoleDto> dto = new HashSet<>();
        for (Permission permission : Permission.getAllPermissions()) {
            dto.add(Permission.RoleDto.builder()
                    .code(permission.getCode())
                    .name(MessageSourceUtils.getMessageOrElseCode(permission.getCode()))
                    .roleHas(role.getPermissions().contains(permission))
                    .currentUserHas(userPermissions.contains(permission))
                    .build());
        }
        return dto;
    }
}
