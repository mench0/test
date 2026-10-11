package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.PaymentScreenLogo;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PaymentScreenLogoMapper implements EntityMapper<PaymentScreenLogo> {

    @Override
    public Map<String, Object> toMap(PaymentScreenLogo entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", entity.getName());
        map.put("imageId", entity.getImageId());

        return map;
    }

    @Override
    public Class<PaymentScreenLogo> getEntityClass() {
        return PaymentScreenLogo.class;
    }
}
