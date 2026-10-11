package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.MerchantController;
import com.tms.profilemanager.dto.MerchantDto;
import com.tms.profilemanager.dto.create.MerchantCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MerchantGenerator {

    MerchantController merchantController;

    @RunAsSystemUser
    public MerchantDto createAndSave(int number) {
        return merchantController.create(buildCreateDto(number));
    }

    public MerchantCreateDto buildCreateDto(int number) {
        return MerchantCreateDto.builder()
                .name("name" + number)
                .address("address" + number)
                .phone("+3749012345" + number)
                .email("123" + number + "@ya.ru")
                .innOrPinfl("123456" + number)
                .build();
    }
}
