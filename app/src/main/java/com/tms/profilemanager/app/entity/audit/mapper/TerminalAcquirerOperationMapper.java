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
public class TerminalAcquirerOperationMapper implements EntityMapper<TerminalAcquirerOperation> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(TerminalAcquirerOperation entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("operation", entity.getOperation());
        map.put("operationOrder", entity.getOperationOrder());
        map.put("acquirer", getReferenceName(
                entityManager,
                TerminalAcquirerParams.class,
                entity.getTerminalAcquirerParams(),
                (params) -> getReferenceName(entityManager, Acquirer.class, params.getAcquirerId(), Acquirer::getName)));

        return map;
    }

    @Override
    public Class<TerminalAcquirerOperation> getEntityClass() {
        return TerminalAcquirerOperation.class;
    }
}
