package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.EMVApp;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class EMVAppMapper implements EntityMapper<EMVApp> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(EMVApp entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("aid", entity.getAid());
        map.put("terminalCapabilities", entity.getTerminalCaps());
        map.put("additionalCapabilities", entity.getTerminalAdditionalCaps());
        map.put("tacDefault", entity.getTacDefault());
        map.put("tacOnline", entity.getTacOnline());
        map.put("tacDenial", entity.getTacDenial());
        map.put("tdol", entity.getTdol());
        map.put("ddol", entity.getDdol());
        map.put("paymentSystem", getReferenceName(entityManager, PaymentSystem.class, entity.getPaymentSystemId(), PaymentSystem::getName));
        map.put("terminalType", entity.getTerminalType());
        return map;
    }

    @Override
    public Class<EMVApp> getEntityClass() {
        return EMVApp.class;
    }
}
