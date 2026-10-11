package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Vendor;
import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class VendorMapper implements EntityMapper<Vendor> {

    @Override
    public Map<String, Object> toMap(Vendor entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", entity.getName());
        if (entity.getModels() != null) {
            map.put("models", entity.getModels().stream()
                    .map(VendorModel::getName)
                    .toList());
        }

        return map;
    }

    @Override
    public Class<Vendor> getEntityClass() {
        return Vendor.class;
    }
}
