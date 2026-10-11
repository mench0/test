package com.tms.profilemanager.app.test.utils.generator;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.ContactlessLimitController;
import com.tms.profilemanager.dto.ContactlessLimitDto;
import com.tms.profilemanager.dto.create.limit.ContactlessLimitCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContactlessLimitGenerator {

    ContactlessLimitController contactlessLimitController;

    @RunAsSystemUser
    public ContactlessLimitDto createAndSave(int number, List<Long> drlIdList) {
        return contactlessLimitController.create(buildCreateDto(number, drlIdList));
    }

    public ContactlessLimitCreateDto buildCreateDto(int number, List<Long> drlIdList) {
        return ContactlessLimitCreateDto.builder()
                .name("a" + number)
                .currency(CurrencyCode.RUB)
                .floorLimit((long) number)
                .noCvmLimit((long) number)
                .onDeviceCvmLimit((long) number)
                .noDeviceCvmLimit((long) number)
                .drlIdList(drlIdList)
                .maxAmount((long) number)
                .build();
    }
}
