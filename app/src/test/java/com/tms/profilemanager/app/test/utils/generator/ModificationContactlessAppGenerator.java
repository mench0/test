package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.ModificationContactlessAppController;
import com.tms.profilemanager.dto.create.modification.ModificationContactlessAppCreateDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.modification.FieldModificationDto;
import com.tms.profilemanager.dto.modification.ModificationContactlessAppDto;
import com.tms.profilemanager.dto.modification.SetModificationDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationContactlessAppGenerator {

    ModificationContactlessAppController modificationContactlessAppController;

    @RunAsSystemUser
    public ModificationContactlessAppDto createAndSave(Long paymentSystemId, Long groupId, Long contactlessAppId) {
        return modificationContactlessAppController.create(buildCreateDto(paymentSystemId, groupId, contactlessAppId));
    }

    public ModificationContactlessAppCreateDto buildCreateDto(Long paymentSystemId, Long groupId, Long contactlessAppId) {
        return ModificationContactlessAppCreateDto.builder()
                .paymentSystemId(paymentSystemId)
                .groupId(groupId)
                .contactlessAppsId(contactlessAppId)
                .aid("111")
                .kernelConfiguration("222")
                .kernelId("33")
                .terminalAdditionalCaps("444")
                .terminalCaps("555")
                .terminalType(new FieldModificationDto<TerminalType>(TerminalType.ATTENDENT_11, false))
                .terminalCapsCVMNotRequired("666")
                .tacDefault("777")
                .tacDenial("888")
                .tacOnline("999")
                .tags(SetModificationDto.<String>builder().value(Set.of("1001")).ignored(false).build())
                .build();
    }
}
