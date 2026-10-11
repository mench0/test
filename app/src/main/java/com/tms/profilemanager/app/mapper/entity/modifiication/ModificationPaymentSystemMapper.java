package com.tms.profilemanager.app.mapper.entity.modifiication;

import com.tms.profilemanager.app.entity.modification.ModificationPaymentSystem;
import com.tms.profilemanager.app.mapper.entity.EntityMapper;
import com.tms.profilemanager.app.mapper.entity.GeneralLimitMapper;
import com.tms.profilemanager.app.mapper.entity.PaymentSystemMapper;
import com.tms.profilemanager.app.service.GeneralLimitService;
import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.modification.ModificationPaymentSystemDto;
import com.tms.profilemanager.dto.view.PaymentSystemView;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ModificationPaymentSystemMapper implements EntityMapper<ModificationPaymentSystem, ModificationPaymentSystemDto> {

    @Autowired
    @Lazy
    PaymentSystemMapper paymentSystemMapper;
    @Autowired
    @Lazy
    ModificationContactlessAppMapper modificationContactlessAppMapper;
    @Autowired
    @Lazy
    ModificationEmvAppMapper modificationEmvAppMapper;
    @Autowired
    @Lazy
    ModificationPaymentSystemMapper modificationPaymentSystemMapper;
    @Autowired
    @Lazy
    GeneralLimitMapper generalLimitMapper;
    @Autowired
    @Lazy
    GeneralLimitService generalLimitService;

    public void paymentSystemModificationMapping(List<ModificationPaymentSystem> modificationPaymentSystems, Set<PaymentSystemView> paymentSystemViews) {
        paymentSystemViews.forEach(paymentSystemView -> modificationPaymentSystems.stream()
                .filter(modificationPaymentSystem -> paymentSystemView.getId().equals(modificationPaymentSystem.getPaymentSystemId()))
                .forEach(modificationPaymentSystem -> patchModificationPaymentSystem(modificationPaymentSystem, paymentSystemView)));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emvApps", ignore = true)
    @Mapping(target = "contactlessApps", ignore = true)
    @Mapping(target = "defaultLimits", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchModificationPaymentSystem(ModificationPaymentSystem modificationPaymentSystem, @MappingTarget PaymentSystemView paymentSystemView);

    @AfterMapping
    public void afterPatchModificationPaymentSystem(ModificationPaymentSystem modificationPaymentSystem, @MappingTarget PaymentSystemView paymentSystemView) {
        if (!modificationPaymentSystem.getEmvApps().isEmpty() && !paymentSystemView.getEmvApps().isEmpty()) {
            modificationPaymentSystem.getEmvApps().forEach(modificationEmvApp -> paymentSystemView.getEmvApps().stream()
                    .filter(emvAppDto -> emvAppDto.getId().equals(modificationEmvApp.getEmvAppId()))
                    .forEach(emvAppDto -> modificationEmvAppMapper.patchModificationEmvApp(modificationEmvApp, emvAppDto)));
        }
        if (!modificationPaymentSystem.getContactlessApps().isEmpty() && !paymentSystemView.getContactlessApps().isEmpty()) {
            modificationPaymentSystem.getContactlessApps().forEach(modificationContactlessApp -> paymentSystemView.getContactlessApps().stream()
                    .filter(contactlessAppDto -> contactlessAppDto.getId().equals(modificationContactlessApp.getContactlessAppId()))
                    .forEach(contactlessAppDto -> modificationContactlessAppMapper.patchModificationContactlessApp(modificationContactlessApp, contactlessAppDto)));
        }
        if (!modificationPaymentSystem.getLimitIds().isEmpty()) {
            List<GeneralLimitDto> limits = generalLimitMapper.toDtoList(generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(modificationPaymentSystem.getLimitIds()).stream().toList());
            if(limits.size() > 0) {
                GeneralLimitDto generalLimitDto = limits.get(0);
                paymentSystemView.setDefaultLimits(new PaymentSystemView.DefaultLimitsDto(generalLimitDto.getContactLimit(), generalLimitDto.getContactlessLimit()));
            }
        }
    }

    @Mapping(target = "groupId", ignore = true)
    public abstract ModificationPaymentSystemDto toDto(ModificationPaymentSystem modificationPaymentSystem);

    @AfterMapping
    protected void afterToDto(@MappingTarget ModificationPaymentSystemDto.ModificationPaymentSystemDtoBuilder<?, ?> dto, ModificationPaymentSystem modificationPaymentSystem) {
        dto.groupId(modificationPaymentSystem.getGroupId());
        dto.paymentSystemId(modificationPaymentSystem.getPaymentSystemId());
        dto.limits(generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(modificationPaymentSystem.getLimitIds()).stream().map(it -> ModificationPaymentSystemDto.ModificationGeneralLimitDto.builder()
                        .id(it.getId())
                        .name(it.getName())
                        .build())
                .collect(Collectors.toSet()));
    }
}
