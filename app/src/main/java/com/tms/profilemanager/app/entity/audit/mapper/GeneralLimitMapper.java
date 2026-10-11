package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.ContactLimit;
import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GeneralLimitMapper implements EntityMapper<GeneralLimit> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(GeneralLimit entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", entity.getName());
        map.put("contactLimit", getReferenceName(entityManager, ContactLimit.class, entity.getContactLimitId(), ContactLimit::getName));
        map.put("contactlessLimit", getReferenceName(entityManager, ContactlessLimit.class, entity.getContactlessLimitId(), ContactlessLimit::getName));
        return map;
    }

    @Override
    public Class<GeneralLimit> getEntityClass() {
        return GeneralLimit.class;
    }
}
