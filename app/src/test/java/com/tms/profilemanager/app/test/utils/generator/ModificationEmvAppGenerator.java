package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.ModificationEmvAppController;
import com.tms.profilemanager.dto.create.modification.ModificationEmvAppCreateDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.modification.FieldModificationDto;
import com.tms.profilemanager.dto.modification.ModificationEmvAppDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationEmvAppGenerator {

    ModificationEmvAppController modificationEmvAppController;

    @RunAsSystemUser
    public ModificationEmvAppDto createAndSave(Long paymentSystemId, Long groupId, Long emvAppsId) {
        return modificationEmvAppController.create(buildCreateDto(paymentSystemId, groupId, emvAppsId));
    }

    public ModificationEmvAppCreateDto buildCreateDto(Long paymentSystemId, Long groupId, Long emvAppsId) {
        return ModificationEmvAppCreateDto.builder()
                .paymentSystemId(paymentSystemId)
                .groupId(groupId)
                .emvAppsId(emvAppsId)
                .aid("111")
                .ddol("222")
                .tdol("333")
                .terminalAdditionalCaps("444")
                .terminalCaps("555")
                .terminalType(new FieldModificationDto<>(TerminalType.ATTENDENT_11, false))
                .tacDefault("666")
                .tacDenial("777")
                .tacOnline("888")
                .build();
    }
}
