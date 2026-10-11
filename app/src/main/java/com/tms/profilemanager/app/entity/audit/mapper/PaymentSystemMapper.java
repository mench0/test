package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PaymentSystemMapper implements EntityMapper<PaymentSystem> {

    @Override
    public Map<String, Object> toMap(PaymentSystem entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("name", entity.getName());
        map.put("avn", entity.getAvn());
        map.put("contactlessAvn", entity.getContactlessAvn());
        map.put("limits", entity.getLimits().stream().map(GeneralLimit::getName).toList());

        return map;
    }

    @Override
    public Class<PaymentSystem> getEntityClass() {
        return PaymentSystem.class;
    }
}
