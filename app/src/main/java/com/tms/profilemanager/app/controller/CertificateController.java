package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.Certificate;
import com.tms.profilemanager.app.mapper.entity.CertificateMapper;
import com.tms.profilemanager.app.service.CertificateService;
import com.tms.profilemanager.dto.CertificateDto;
import com.tms.profilemanager.dto.create.CertificateCreateDto;
import com.tms.profilemanager.dto.enums.sort.CertificateSort;
import com.tms.profilemanager.dto.filter.CertificateFilterDto;
import com.tms.profilemanager.dto.patch.CertificatePatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificate")
@Tag(name = "Certificates", description = "Сертификаты")
public class CertificateController extends
        AbstractCrudController<Certificate, CertificateDto, CertificateCreateDto, CertificatePatchDto, CertificateFilterDto, CertificateSort> {

    public CertificateController(CertificateService service, CertificateMapper mapper) {
        super(service, mapper);
    }
}
