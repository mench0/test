package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.service.CertificateService;
import com.tms.profilemanager.app.service.ContactlessAppService;
import com.tms.profilemanager.app.service.EMVAppService;
import com.tms.profilemanager.app.service.GeneralLimitService;
import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.PaymentSystemDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.view.PaymentSystemView;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring", uses = {ContactlessLimitMapper.class, ContactLimitMapper.class, GeneralLimitMapper.class})
@Slf4j
public abstract class PaymentSystemMapper implements EntityMapper<PaymentSystem, PaymentSystemDto> {

    @Autowired
    CertificateService certificateService;
    @Autowired
    CertificateMapper certificateMapper;
    @Autowired
    EMVAppService emvAppService;
    @Autowired
    EMVAppMapper emvAppMapper;
    @Autowired
    ContactlessAppService contactlessAppService;
    @Autowired
    GeneralLimitService generalLimitService;
    @Autowired
    ContactlessAppMapper contactlessAppMapper;
    @Autowired
    GeneralLimitMapper generalLimitMapper;

    public abstract PaymentSystemView toView(PaymentSystem entity, @Context TerminalType terminalType);

    @AfterMapping
    protected void afterToView(@MappingTarget PaymentSystemView.PaymentSystemViewBuilder<?, ?> dto, PaymentSystem entity, @Context TerminalType terminalType) {
        dto.certificates(certificateService.getByPaymentSystemIdUsingCacheWithoutAccessControl(entity.getId()).stream()
                .filter(certificate -> !certificate.isArchived())
                .map(certificateMapper::toDto)
                .collect(toSet()));
        dto.emvApps(emvAppService.getByPaymentSystemIdUsingCacheWithoutAccessControl(entity.getId()).stream()
                .filter(emvApp -> !emvApp.isArchived())
                .filter(emvApp -> terminalType.equals(emvApp.getTerminalType()) || emvApp.getTerminalType() == null)
                .map(emvAppMapper::toDto)
                .collect(toSet()));
        dto.contactlessApps(contactlessAppService.getByPaymentSystemIdAndNotArchived(entity.getId()).stream()
                .map(contactlessAppMapper::toDto)
                .filter(contactlessApp -> terminalType.equals(contactlessApp.getTerminalType()) || contactlessApp.getTerminalType() == null)
                .collect(toSet()));
        List<GeneralLimit> generalLimitsDto = new ArrayList<GeneralLimit>(generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(entity.getLimits().stream()
                .map(BaseEntity::getId)
                .collect(toSet())));
        GeneralLimitDto generalLimitDto = generalLimitMapper.toDto(generalLimitsDto.get(0));
        dto.defaultLimits(new PaymentSystemView.DefaultLimitsDto(generalLimitDto.getContactLimit(), generalLimitDto.getContactlessLimit()));
    }
}
