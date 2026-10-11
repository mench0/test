package com.tms.profilemanager.app.test.utils.generator;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.ContactLimitController;
import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.create.limit.ContactLimitCreateDto;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
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
public class ContactLimitGenerator {

    ContactLimitController contactLimitController;

    @RunAsSystemUser
    public ContactLimitDto createAndSave(int number) {
        return contactLimitController.create(buildCreateDto(number));
    }

    public ContactLimitCreateDto buildCreateDto(int number) {
        return ContactLimitCreateDto.builder()
                .name("b" + number)
                .currency(CurrencyCode.RUB)
                .threshold((long) number)
                .targetPercent(number)
                .maxTargetPercent(number)
                .floorLimit((long) number)
                .pinBypassAllowed(true)
                .pinLimit(number)
                .requestLast4DigitsPAN(Set.of(AcquirerOperationType.SALE))
                .build();
    }
}
