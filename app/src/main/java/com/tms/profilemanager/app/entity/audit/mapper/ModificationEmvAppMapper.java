package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.EMVApp;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import com.tms.profilemanager.app.entity.modification.ModificationEmvApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ModificationEmvAppMapper implements EntityMapper<ModificationEmvApp> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(ModificationEmvApp entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("aid", entity.getAid());
        map.put("terminalCapabilities", entity.getTerminalCaps());
        map.put("additionalCapabilities", entity.getTerminalAdditionalCaps());
        map.put("tacDefault", entity.getTacDefault());
        map.put("tacOnline", entity.getTacOnline());
        map.put("tacDenial", entity.getTacDenial());
        map.put("tdol", entity.getTdol());
        map.put("ddol", entity.getDdol());
        map.put("paymentSystem", getReferenceName(entityManager, EMVApp.class, entity.getEmvAppId(),
                it -> getReferenceName(entityManager, PaymentSystem.class, it.getPaymentSystemId(), PaymentSystem::getName)));
        if (entity.getTerminalType() != null) {
            map.put("terminalTypeValue", entity.getTerminalType().getValue());
            map.put("terminalTypeIgnored", entity.getTerminalType().isIgnored());
        }

        return map;
    }

    @Override
    public Class<ModificationEmvApp> getEntityClass() {
        return ModificationEmvApp.class;
    }
}
