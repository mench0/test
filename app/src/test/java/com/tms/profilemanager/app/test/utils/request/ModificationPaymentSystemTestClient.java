package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.create.modification.ModificationPaymentSystemCreateDto;
import com.tms.profilemanager.dto.filter.ModificationPaymentSystemFilterDto;
import com.tms.profilemanager.dto.modification.ModificationPaymentSystemDto;
import com.tms.profilemanager.dto.patch.modification.ModificationPaymentSystemPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ModificationPaymentSystemTestClient extends CrudTestClient<ModificationPaymentSystemDto, ModificationPaymentSystemCreateDto, ModificationPaymentSystemPatchDto, ModificationPaymentSystemFilterDto> {

    public ModificationPaymentSystemTestClient(RequestSender requestSender) {
        super(ModificationPaymentSystemDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<ModificationPaymentSystemDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/modification-payment-system";
    }
}
