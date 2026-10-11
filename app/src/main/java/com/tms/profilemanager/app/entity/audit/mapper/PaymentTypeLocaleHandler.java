package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.PaymentTypeLocale;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PaymentTypeLocaleHandler implements EntityMapper<PaymentTypeLocale> {

    @Autowired
    EntityManager entityManager;

    @Override
    public Map<String, Object> toMap(PaymentTypeLocale entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("paymentName", entity.getPaymentName());
        map.put("locale", entity.getLocale());
        map.put("paymentType", getReferenceName(entityManager, PaymentTypeLocale.class, entity.getPaymentTypeId(), PaymentTypeLocale::getPaymentName));

        return map;
    }

    @Override
    public Class<PaymentTypeLocale> getEntityClass() {
        return PaymentTypeLocale.class;
    }
}
