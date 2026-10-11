package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import com.tms.profilemanager.app.entity.modification.ModificationTerminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ModificationTerminalMapper implements EntityMapper<ModificationTerminal> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(ModificationTerminal entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("country", entity.getCountry());
        map.put("servicePin", entity.getServicePin());
        map.put("group", getReferenceName(entityManager, TerminalGroup.class, entity.getGroupId(), TerminalGroup::getName));
        return map;
    }

    @Override
    public Class<ModificationTerminal> getEntityClass() {
        return ModificationTerminal.class;
    }
}
