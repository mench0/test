package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.app.exception.EntityNotFoundByIdException;
import com.tms.profilemanager.app.mapper.entity.PermissionMapper;
import com.tms.profilemanager.dto.enums.Permission;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    RoleService roleService;
    PermissionMapper permissionMapper;

    @PostConstruct
    public void checkPermissions() {
        Permission.assertThatAllNamesAreUnique();
    }

    public Set<Permission.RoleDto> getPermissionsByRoleId(Long roleId) {
        return roleService.getById(roleId)
                .map(permissionMapper::toRoleDto)
                .orElseThrow(() -> new EntityNotFoundByIdException(Role.class, roleId));
    }
}
