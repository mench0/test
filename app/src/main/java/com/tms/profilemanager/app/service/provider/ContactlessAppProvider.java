package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.ContactlessApp;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.PaymentSystemService;
import com.tms.profilemanager.dto.create.ContactlessAppCreateDto;
import com.tms.profilemanager.dto.patch.ContactlessAppPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.HashSet;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class ContactlessAppProvider extends EntityProvider<ContactlessApp, ContactlessAppCreateDto, ContactlessAppPatchDto> {

    @Autowired @Lazy PaymentSystemService paymentSystemService;

    @AfterMapping
    protected void afterInitializeEntity(ContactlessAppCreateDto source, @MappingTarget ContactlessApp target) {
        paymentSystemService.existsByIdOrElseThrow(source.getPaymentSystemId());
        target.setPaymentSystemId(source.getPaymentSystemId());
    }

    @Override
    @Mapping(target = "tags", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(ContactlessAppPatchDto source, @MappingTarget ContactlessApp target);

    @AfterMapping
    protected void afterPatchEntity(ContactlessAppPatchDto source, @MappingTarget ContactlessApp target) {
        if (source.getTags() != null) {
            target.setTags(source.getTags().map(HashSet::new).orElse(new HashSet<>()));
        }
        if (source.getPaymentSystemId() != null) {
            source.getPaymentSystemId().ifPresent(paymentSystemService::existsByIdOrElseThrow);
            target.setPaymentSystemId(source.getPaymentSystemId().orElse(null));
        }
    }

    @Override
    protected Class<ContactlessApp> getEntityClass() {
        return ContactlessApp.class;
    }
}
