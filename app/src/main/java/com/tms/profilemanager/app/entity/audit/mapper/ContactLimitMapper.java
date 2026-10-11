package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.ContactLimit;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ContactLimitMapper implements EntityMapper<ContactLimit> {

    @Override
    public Map<String, Object> toMap(ContactLimit entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("name", entity.getName());
        map.put("currency", entity.getCurrency());
        map.put("threshold", entity.getThreshold());
        map.put("targetPercent", entity.getTargetPercent());
        map.put("maxTargetPercent", entity.getMaxTargetPercent());
        map.put("floorLimit", entity.getFloorLimit());
        map.put("pinBypassAllowed", entity.getPinBypassAllowed());
        map.put("pinLimit", entity.getPinLimit());
        map.put("requestLast4DigitsPAN", entity.getRequestLast4DigitsPAN());

        return map;
    }

    @Override
    public Class<ContactLimit> getEntityClass() {
        return ContactLimit.class;
    }
}
