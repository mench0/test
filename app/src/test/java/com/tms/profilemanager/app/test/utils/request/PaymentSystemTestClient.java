package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.PaymentSystemDto;
import com.tms.profilemanager.dto.create.PaymentSystemCreateDto;
import com.tms.profilemanager.dto.filter.PaymentSystemFilterDto;
import com.tms.profilemanager.dto.patch.PaymentSystemPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaymentSystemTestClient extends
        CrudTestClient<PaymentSystemDto, PaymentSystemCreateDto, PaymentSystemPatchDto, PaymentSystemFilterDto> {

    public PaymentSystemTestClient(RequestSender requestSender) {
        super(PaymentSystemDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<PaymentSystemDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/payment-system";
    }
}
