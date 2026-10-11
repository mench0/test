package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.TerminalAcquirerOperation;
import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TerminalAcquirerParamsMapper implements EntityMapper<TerminalAcquirerParams> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(TerminalAcquirerParams entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("merchant name", entity.getMerchantName());
        map.put("isoTerminalId", entity.getIsoTerminalId());
        map.put("isoMerchantId", entity.getIsoMerchantId());
        map.put("pinMasterKeyKCV", entity.getPinMasterKeyKCV());
        map.put("kcvUpdatedDate", entity.getKcvUpdatedDate());
        map.put("rkiEnabled", entity.isRkiEnabled());
        map.put("terminalType", entity.getType());
        map.put("mcc", entity.getMcc());
        map.put("availableOperations", entity.getAvailableOperations().stream()
                .map(TerminalAcquirerOperation::getOperation)
                .toList());
        map.put("paramsPaymentTypes", entity.getParamsPaymentTypes());
        map.put("acquirer", getReferenceName(entityManager, Acquirer.class, entity.getAcquirerId(), Acquirer::getName));
        map.put("order", entity.getParamsOrder());

        return map;
    }

    @Override
    public Class<TerminalAcquirerParams> getEntityClass() {
        return TerminalAcquirerParams.class;
    }
}
