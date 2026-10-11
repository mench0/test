package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.ContactlessAppController;
import com.tms.profilemanager.dto.ContactlessAppDto;
import com.tms.profilemanager.dto.create.ContactlessAppCreateDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContactlessAppGenerator {

    ContactlessAppController contactlessAppController;

    @RunAsSystemUser
    public ContactlessAppDto createAndSave(int number, Long paymentSystemId, TerminalType terminalType) {
        return contactlessAppController.create(buildCreateDto(number, paymentSystemId, terminalType));
    }

    public ContactlessAppCreateDto buildCreateDto(int number, Long paymentSystemId, TerminalType terminalType) {
        String numberStr = Integer.valueOf(number).toString();
        return ContactlessAppCreateDto.builder()
                .aid("aid" + number)
                .kernelId(numberStr)
                .kernelConfiguration(numberStr)
                .terminalCaps(numberStr)
                .terminalCapsCVMNotRequired(numberStr)
                .terminalAdditionalCaps(numberStr)
                .tacDefault(numberStr)
                .tacOnline(numberStr)
                .tacDenial(numberStr)
                .paymentSystemId(paymentSystemId)
                .tags(new HashSet<>(Set.of(numberStr)))
                .terminalType(terminalType)
                .build();
    }
}
