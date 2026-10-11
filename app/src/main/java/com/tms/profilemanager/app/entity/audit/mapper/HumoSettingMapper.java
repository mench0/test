package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class HumoSettingMapper implements EntityMapper<HumoSetting> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(HumoSetting entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("humoId", entity.getHumoId());
        map.put("terminalType", entity.getTerminalType());
        map.put("servicePin", entity.getServicePin());
        map.put("model", getReferenceName(entityManager, VendorModel.class, entity.getModelId(), VendorModel::getName));
        map.put("acquirer", getReferenceName(entityManager, Acquirer.class, entity.getAcquirerId(), Acquirer::getName));
        return map;
    }

    @Override
    public Class<HumoSetting> getEntityClass() {
        return HumoSetting.class;
    }
}
