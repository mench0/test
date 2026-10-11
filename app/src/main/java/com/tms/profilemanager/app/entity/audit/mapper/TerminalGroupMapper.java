package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TerminalGroupMapper implements EntityMapper<TerminalGroup> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(TerminalGroup entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("name", entity.getName());
        map.put("parent", getReferenceName(entityManager, TerminalGroup.class, entity.getParentId(), TerminalGroup::getName));
        map.put("logos", entity.getPaymentScreenLogos().stream().map(it -> it.getPaymentScreenLogo().getName()).toList());

        return map;
    }

    @Override
    public Class<TerminalGroup> getEntityClass() {
        return TerminalGroup.class;
    }
}
