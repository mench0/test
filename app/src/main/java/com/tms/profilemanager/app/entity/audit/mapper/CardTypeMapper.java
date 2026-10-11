package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.audit.EntityMapper;
import com.tms.profilemanager.app.entity.dictionary.CardType;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CardTypeMapper implements EntityMapper<CardType> {
    @Override
    public Map<String, Object> toMap(CardType entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", entity.getName());
        return map;
    }

    @Override
    public Class<CardType> getEntityClass() {
        return CardType.class;
    }
}
