package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import com.tms.profilemanager.app.entity.modification.ModificationAcquirer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ModificationAcquirerMapper implements EntityMapper<ModificationAcquirer> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(ModificationAcquirer entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("cashbackLimit", entity.getCashbackLimit());
        map.put("acquirer", getReferenceName(entityManager, Acquirer.class, entity.getAcquirerId(), Acquirer::getName));
        map.put("group", getReferenceName(entityManager, TerminalGroup.class, entity.getGroupId(), TerminalGroup::getName));
        if (entity.getKeySettings() != null) {
            Map<String, Object> keySettings = new HashMap<>();
            keySettings.put("pinMasterKeyIndex", entity.getKeySettings().getPinMasterKeyIndex());
            keySettings.put("pinWorkKeyIndex", entity.getKeySettings().getPinWorkKeyIndex());
            keySettings.put("macMasterKeyIndex", entity.getKeySettings().getMacMasterKeyIndex());
            keySettings.put("macWorkingKeyIndex", entity.getKeySettings().getMacWorkingKeyIndex());
            keySettings.put("keyScheme", entity.getKeySettings().getKeyScheme());
            map.put("keySettings", keySettings);
        }

        if (entity.getPaymentHost() != null) {
            Map<String, Object> paymentHost = new HashMap<>();
            paymentHost.put("host", entity.getPaymentHost().getHost());
            paymentHost.put("port", entity.getPaymentHost().getPort());
            paymentHost.put("useSsl", entity.getPaymentHost().getUseSsl());
            map.put("paymentHost", paymentHost);
        }

        return map;
    }

    @Override
    public Class<ModificationAcquirer> getEntityClass() {
        return ModificationAcquirer.class;
    }
}
