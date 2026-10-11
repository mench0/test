package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Merchant;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TerminalMapper implements EntityMapper<Terminal> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(Terminal entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("serial number", entity.getSerialNumber());
        map.put("model", getReferenceName(entityManager, VendorModel.class, entity.getVendorModelId(), VendorModel::getName));
        map.put("defaultTerminalType", entity.getDefaultTerminalType());
        map.put("merchant", getReferenceName(entityManager, Merchant.class, entity.getMerchantId(), Merchant::getName));
        map.put("servicePin", entity.getServicePin());
        map.put("country", entity.getCountry());
        map.put("group", getReferenceName(entityManager, TerminalGroup.class, entity.getGroupId(), TerminalGroup::getName));
        map.put("address", entity.getAddress());
        map.put("softwareVersion", entity.getSoftwareVersion());
        map.put("softwareName", entity.getSoftwareName());
        map.put("mode", entity.getMode());
        map.put("softwareVendor", entity.getSoftwareVendor());
        map.put("softwareModel", entity.getSoftwareModel());

        return map;
    }

    @Override
    public Class<Terminal> getEntityClass() {
        return Terminal.class;
    }
}
