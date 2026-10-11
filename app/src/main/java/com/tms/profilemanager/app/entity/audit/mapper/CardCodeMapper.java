package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.audit.EntityMapper;
import com.tms.profilemanager.app.entity.dictionary.CardType;
import com.tms.profilemanager.app.entity.dictionary.CardCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CardCodeMapper implements EntityMapper<CardCode> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(CardCode entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", entity.getCode());
        map.put("card type", getReferenceName(entityManager, CardType.class, entity.getCardTypeId(), CardType::getName));
        return map;
    }

    @Override
    public Class<CardCode> getEntityClass() {
        return CardCode.class;
    }
}
