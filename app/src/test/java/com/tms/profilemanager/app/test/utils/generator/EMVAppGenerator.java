package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.EMVAppController;
import com.tms.profilemanager.dto.EMVAppDto;
import com.tms.profilemanager.dto.create.EMVAppCreateDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EMVAppGenerator {

    EMVAppController emvAppController;

    @RunAsSystemUser
    public EMVAppDto createAndSave(int number, Long paymentSystemId, TerminalType terminalType) {
        return emvAppController.create(buildCreateDto(number, paymentSystemId, terminalType));
    }

    public EMVAppCreateDto buildCreateDto(int number, Long paymentSystemId, TerminalType terminalType) {
        String numberStr = Integer.valueOf(number).toString();
        return EMVAppCreateDto.builder()
                .aid("aid" + number)
                .terminalCaps(numberStr)
                .terminalAdditionalCaps(numberStr)
                .tacDefault(numberStr)
                .tacOnline(numberStr)
                .tacDenial(numberStr)
                .tdol("tdol" + number)
                .ddol("ddol" + number)
                .paymentSystemId(paymentSystemId)
                .terminalType(terminalType)
                .build();
    }
}
