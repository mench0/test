package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import com.tms.profilemanager.app.entity.modification.ModificationContactlessApp;
import com.tms.profilemanager.app.entity.modification.ModificationEmvApp;
import com.tms.profilemanager.app.entity.modification.ModificationPaymentSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ModificationPaymentSystemMapper implements EntityMapper<ModificationPaymentSystem> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(ModificationPaymentSystem entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("avn", entity.getAvn());
        map.put("contactlessAvn", entity.getContactlessAvn());
        map.put("paymentSystem", getReferenceName(entityManager, PaymentSystem.class, entity.getGroupId(), PaymentSystem::getName));
        map.put("group", getReferenceName(entityManager, TerminalGroup.class, entity.getGroupId(), TerminalGroup::getName));
        map.put("limits", getReferenceNames(entityManager, GeneralLimit.class, entity.getLimitIds().stream().toList(), GeneralLimit::getName));

        List<Map<String, Object>> emvApps = entity.getEmvApps().stream()
                .map(this::mapEmvApp)
                .collect(Collectors.toList());
        map.put("emvApps", emvApps);

        List<Map<String, Object>> contactlessApps = entity.getContactlessApps().stream()
                .map(this::mapContactlessApp)
                .collect(Collectors.toList());
        map.put("contactlessApps", contactlessApps);

        return map;
    }

    private Map<String, Object> mapEmvApp(ModificationEmvApp app) {
        Map<String, Object> map = new HashMap<>();
        map.put("emvAppId", app.getId());
        return map;
    }

    private Map<String, Object> mapContactlessApp(ModificationContactlessApp app) {
        Map<String, Object> map = new HashMap<>();
        map.put("contactlessAppId", app.getId());
        return map;
    }

    @Override
    public Class<ModificationPaymentSystem> getEntityClass() {
        return ModificationPaymentSystem.class;
    }
}
