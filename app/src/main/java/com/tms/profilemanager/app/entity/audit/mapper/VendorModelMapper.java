package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Vendor;
import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class VendorModelMapper implements EntityMapper<VendorModel> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(VendorModel entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", entity.getName());
        map.put("vendors", getReferenceName(entityManager, Vendor.class, entity.getVendorId(), Vendor::getName));
        return map;
    }

    @Override
    public Class<VendorModel> getEntityClass() {
        return VendorModel.class;
    }
}
