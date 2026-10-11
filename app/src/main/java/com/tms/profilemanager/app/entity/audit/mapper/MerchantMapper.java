package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Merchant;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class MerchantMapper implements EntityMapper<Merchant> {

    @Override
    public Map<String, Object> toMap(Merchant entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("name", entity.getName());
        map.put("legal address", entity.getAddress());

        return map;
    }

    @Override
    public Class<Merchant> getEntityClass() {
        return Merchant.class;
    }
}
