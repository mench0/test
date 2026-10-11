package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.License;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LicenseMapper implements EntityMapper<License> {

    @Override
    public Map<String, Object> toMap(License entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("serialNumber", entity.getSerialNumber());
        map.put("licenseCode", entity.getLicenseCode());
        return map;
    }

    @Override
    public Class<License> getEntityClass() {
        return License.class;
    }
}
