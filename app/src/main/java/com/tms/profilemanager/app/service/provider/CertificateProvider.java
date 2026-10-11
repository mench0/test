package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.Certificate;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.PaymentSystemService;
import com.tms.profilemanager.dto.create.CertificateCreateDto;
import com.tms.profilemanager.dto.patch.CertificatePatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class CertificateProvider extends EntityProvider<Certificate, CertificateCreateDto, CertificatePatchDto> {

    @Autowired @Lazy PaymentSystemService paymentSystemService;

    @AfterMapping
    protected void afterInitializeEntity(CertificateCreateDto source, @MappingTarget Certificate target) {
        paymentSystemService.existsByIdOrElseThrow(source.getPaymentSystemId());
        target.setPaymentSystemId(source.getPaymentSystemId());
    }

    @AfterMapping
    protected void afterPatchEntity(CertificatePatchDto source, @MappingTarget Certificate target) {
        if (source.getPaymentSystemId() != null) {
            source.getPaymentSystemId().ifPresent(paymentSystemService::existsByIdOrElseThrow);
            target.setPaymentSystemId(source.getPaymentSystemId().orElse(null));
        }
    }

    @Override
    protected Class<Certificate> getEntityClass() {
        return Certificate.class;
    }
}
