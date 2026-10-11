package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.PaymentTypeDto;
import com.tms.profilemanager.dto.create.PaymentTypeCreateDto;
import com.tms.profilemanager.dto.filter.PaymentTypeFilterDto;
import com.tms.profilemanager.dto.patch.PaymentTypePatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaymentTypeTestClient extends
        CrudTestClient<PaymentTypeDto, PaymentTypeCreateDto, PaymentTypePatchDto, PaymentTypeFilterDto> {

    public PaymentTypeTestClient(RequestSender requestSender) {
        super(PaymentTypeDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<PaymentTypeDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/payment-type";
    }
}
