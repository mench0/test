package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.AcquirerOperation;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AcquirerOperationMapper implements EntityMapper<AcquirerOperation> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(AcquirerOperation entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("operation", entity.getOperation());
        map.put("operationOrder", entity.getOperationOrder());
        map.put("acquirer", getReferenceName(entityManager, Acquirer.class, entity.getAcquirerId(), Acquirer::getName));
        return map;
    }

    @Override
    public Class<AcquirerOperation> getEntityClass() {
        return AcquirerOperation.class;
    }
}
