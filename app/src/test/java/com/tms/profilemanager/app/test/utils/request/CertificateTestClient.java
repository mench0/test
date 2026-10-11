package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.CertificateDto;
import com.tms.profilemanager.dto.create.CertificateCreateDto;
import com.tms.profilemanager.dto.filter.CertificateFilterDto;
import com.tms.profilemanager.dto.patch.CertificatePatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CertificateTestClient extends
        CrudTestClient<CertificateDto, CertificateCreateDto, CertificatePatchDto, CertificateFilterDto> {

    public CertificateTestClient(RequestSender requestSender) {
        super(CertificateDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<CertificateDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/certificate";
    }
}
