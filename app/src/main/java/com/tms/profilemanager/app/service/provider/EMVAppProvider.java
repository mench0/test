package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.EMVApp;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.PaymentSystemService;
import com.tms.profilemanager.dto.create.EMVAppCreateDto;
import com.tms.profilemanager.dto.patch.EMVAppPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class EMVAppProvider extends EntityProvider<EMVApp, EMVAppCreateDto, EMVAppPatchDto> {

    @Autowired @Lazy PaymentSystemService paymentSystemService;

    @AfterMapping
    protected void afterInitializeEntity(EMVAppCreateDto source, @MappingTarget EMVApp target) {
        paymentSystemService.existsByIdOrElseThrow(source.getPaymentSystemId());
        target.setPaymentSystemId(source.getPaymentSystemId());
    }

    @AfterMapping
    protected void afterPatchEntity(EMVAppPatchDto source, @MappingTarget EMVApp target) {
        if (source.getPaymentSystemId() != null) {
            source.getPaymentSystemId().ifPresent(paymentSystemService::existsByIdOrElseThrow);
            target.setPaymentSystemId(source.getPaymentSystemId().orElse(null));
        }
    }

    @Override
    protected Class<EMVApp> getEntityClass() {
        return EMVApp.class;
    }
}
