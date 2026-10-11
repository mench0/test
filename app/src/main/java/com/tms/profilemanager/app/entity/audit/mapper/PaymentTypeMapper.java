package com.tms.profilemanager.app.entity.audit.mapper;

import com.tms.profilemanager.app.entity.PaymentType;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentTypeMapper implements EntityMapper<PaymentType> {

    @Override
    public Map<String, Object> toMap(PaymentType entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("serviceId", entity.getPaymentValue());
        if (entity.getLocalizedNames() != null && !entity.getLocalizedNames().isEmpty()) {
            List<Map<String, Object>> paymentMaps = entity.getLocalizedNames().stream()
                    .map(paymentTypeLocale -> {
                        Map<String, Object> paymentMap = new HashMap<>();
                        paymentMap.put("paymentName", paymentTypeLocale.getPaymentName());
                        paymentMap.put("locale", paymentTypeLocale.getLocale());
                        return paymentMap;
                    })
                    .collect(Collectors.toList());
            map.put("payments", paymentMaps);
        }
        return map;
    }

    @Override
    public Class<PaymentType> getEntityClass() {
        return PaymentType.class;
    }
}
