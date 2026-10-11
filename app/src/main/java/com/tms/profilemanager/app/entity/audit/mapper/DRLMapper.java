package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.app.entity.DRL;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DRLMapper implements EntityMapper<DRL> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(DRL entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("index", entity.getIndex());
        map.put("operationLimit", entity.getOperationLimit());
        map.put("floorLimit", entity.getFloorLimit());
        map.put("noCvmLimit", entity.getNoCvmLimit());
        map.put("contactlessLimit", getReferenceName(entityManager, ContactlessLimit.class, entity.getContactlessLimitId(), ContactlessLimit::getName));
        return map;
    }

    @Override
    public Class<DRL> getEntityClass() {
        return DRL.class;
    }
}
