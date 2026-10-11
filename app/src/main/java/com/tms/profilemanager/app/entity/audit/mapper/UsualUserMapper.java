package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.app.entity.UsualUser;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class UsualUserMapper implements EntityMapper<UsualUser> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(UsualUser entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("username", entity.getUsername());
        map.put("fullName", entity.getFullName());
        map.put("role", getReferenceName(entityManager, Role.class, entity.getRoleId(), Role::getName));
        map.put("passwordHash", entity.getPasswordHash());
        map.put("passwordLastSetAt", entity.getPasswordLastSetAt());
        map.put("email", entity.getEmail());
        if (entity.getUserSettings() != null) {
            map.put("locale", entity.getUserSettings().getLocale());
        }

        return map;
    }

    @Override
    public Class<UsualUser> getEntityClass() {
        return UsualUser.class;
    }
}
