package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.ContactlessApp;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import com.tms.profilemanager.app.entity.modification.ModificationContactlessApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ModificationContactlessAppMapper implements EntityMapper<ModificationContactlessApp> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(ModificationContactlessApp entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("aid", entity.getAid());
        map.put("kernelId", entity.getKernelId());
        map.put("kernelConfiguration", entity.getKernelConfiguration());
        map.put("terminalCapabilities", entity.getTerminalCaps());
        map.put("terminalCapabilities(No CVM)", entity.getTerminalCapsCVMNotRequired());
        map.put("additionalCapabilities", entity.getTerminalAdditionalCaps());
        map.put("tacDefault", entity.getTacDefault());
        map.put("tacOnline", entity.getTacOnline());
        map.put("tacDenial", entity.getTacDenial());
        map.put("paymentSystem", getReferenceName(entityManager, ContactlessApp.class, entity.getContactlessAppId(),
                it -> getReferenceName(entityManager, PaymentSystem.class, it.getPaymentSystemId(), PaymentSystem::getName)));
        if (entity.getTags() != null) {
            map.put("tagsValue", entity.getTags().getValue());
            map.put("tagsIgnored", entity.getTags().isIgnored());
        }

        if (entity.getTerminalType() != null) {
            map.put("terminalTypeValue", entity.getTerminalType().getValue());
            map.put("terminalTypeIgnored", entity.getTerminalType().isIgnored());
        }

        return map;
    }

    @Override
    public Class<ModificationContactlessApp> getEntityClass() {
        return ModificationContactlessApp.class;
    }
}
