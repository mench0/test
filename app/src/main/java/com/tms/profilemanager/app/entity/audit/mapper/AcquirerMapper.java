package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AcquirerMapper implements EntityMapper<Acquirer> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(Acquirer entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("name", entity.getName());
        map.put("usingMacToSignRequests", entity.getUsingMacToSignRequests());
        map.put("isoProtocol", entity.getIsoProtocol());
        map.put("paymentSystems", getReferenceNames(entityManager, PaymentSystem.class, entity.getPaymentSystemIds().stream().toList(), PaymentSystem::getName));
        map.put("availableCurrencies", entity.getAvailableCurrencies());
        map.put("availableOperations", entity.getAvailableOperations());
        map.put("cashbackLimit", entity.getCashbackLimit());
        if (entity.getKeySettings() != null) {
            Map<String, Object> keySettingsMap = new HashMap<>();
            map.put("pinMasterKeyIndex", entity.getKeySettings().getPinMasterKeyIndex());
            map.put("pinWorkKeyIndex", entity.getKeySettings().getPinWorkKeyIndex());
            map.put("macMasterKeyIndex", entity.getKeySettings().getMacMasterKeyIndex());
            map.put("macWorkingKeyIndex", entity.getKeySettings().getMacWorkingKeyIndex());
            map.put("keyScheme", entity.getKeySettings().getKeyScheme());
            map.put("keySettings", keySettingsMap);
        }

        if (entity.getPaymentHost() != null) {
            Map<String, Object> paymentHostMap = new HashMap<>();
            paymentHostMap.put("host", entity.getPaymentHost().getHost());
            paymentHostMap.put("port", entity.getPaymentHost().getPort());
            paymentHostMap.put("useSsl", entity.getPaymentHost().getUseSsl());
            map.put("paymentHost", paymentHostMap);
        }

        if (entity.getRklUrl() != null) {
            Map<String, Object> rklUrlMap = new HashMap<>();
            rklUrlMap.put("rklEnable", entity.getRklUrl().getRklEnable());
            rklUrlMap.put("rklHost", entity.getRklUrl().getRklHost());
            rklUrlMap.put("rklPort", entity.getRklUrl().getRklPort());
            rklUrlMap.put("rklPath", entity.getRklUrl().getRklPath());
            rklUrlMap.put("rklUseSsl", entity.getRklUrl().getRklUseSsl());
            map.put("rklUrl", rklUrlMap);
        }
        return map;
    }

    @Override
    public Class<Acquirer> getEntityClass() {
        return Acquirer.class;
    }
}
