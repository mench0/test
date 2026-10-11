package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.Certificate;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CertificateMapper implements EntityMapper<Certificate> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(Certificate entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("archived", entity.isArchived());
        map.put("index", entity.getIndex());
        map.put("rid", entity.getRid());
        map.put("exponent", entity.getExponent());
        map.put("validUntil", entity.getValidUntil());
        map.put("modulus", entity.getModulus());
        map.put("hash", entity.getHash());
        map.put("paymentSystem", getReferenceName(entityManager, PaymentSystem.class, entity.getPaymentSystemId(), PaymentSystem::getName));
        return map;
    }

    @Override
    public Class<Certificate> getEntityClass() {
        return Certificate.class;
    }
}
