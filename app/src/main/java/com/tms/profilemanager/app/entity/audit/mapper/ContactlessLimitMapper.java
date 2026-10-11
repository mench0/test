package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ContactlessLimitMapper implements EntityMapper<ContactlessLimit> {

    @Override
    public Map<String, Object> toMap(ContactlessLimit entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("name", entity.getName());
        map.put("currency", entity.getCurrency());
        map.put("floorLimit", entity.getFloorLimit());
        map.put("noCvmLimit", entity.getNoCvmLimit());
        map.put("onDeviceCvmLimit", entity.getOnDeviceCvmLimit());
        map.put("noDeviceCvmLimit", entity.getNoDeviceCvmLimit());
        map.put("maxAmount", entity.getMaxAmount());

        if (entity.getDrlList() != null && !entity.getDrlList().isEmpty()) {
            List<Map<String, Object>> drlMaps = entity.getDrlList().stream()
                    .map(drl -> {
                        Map<String, Object> drlMap = new HashMap<>();
                        drlMap.put("id", drl.getId());
                        return drlMap;
                    })
                    .collect(Collectors.toList());
            map.put("drlList", drlMaps);
        }

        return map;
    }

    @Override
    public Class<ContactlessLimit> getEntityClass() {
        return ContactlessLimit.class;
    }
}
