package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RoleMapper implements EntityMapper<Role> {

    @Override
    public Map<String, Object> toMap(Role entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", entity.getName());
        map.put("hasAllPermissions", entity.isHasAllPermissions());
        map.put("systemUserRole", entity.isSystemUserRole());
        map.put("terminalDefaultRole", entity.isTerminalDefaultRole());
        map.put("permissions", entity.getPermissions());

        return map;
    }

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }
}
