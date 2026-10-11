package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.CardRange;
import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CardRangeMapper implements EntityMapper<CardRange> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(CardRange entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("low", entity.getLow());
        map.put("high", entity.getHigh());
        map.put("name", entity.getName());
        map.put("acquirer", getReferenceName(entityManager, Acquirer.class, entity.getAcquirerId(), Acquirer::getName));
        map.put("limits", entity.getLimits().stream().map(GeneralLimit::getName).toList());

        return map;
    }

    @Override
    public Class<CardRange> getEntityClass() {
        return CardRange.class;
    }
}
