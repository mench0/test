package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.LicenseController;
import com.tms.profilemanager.dto.LicenseDto;
import com.tms.profilemanager.dto.create.LicenseCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LicenseGenerator {

    LicenseController licenseController;

    @RunAsSystemUser
    public LicenseDto createAndSave(int number) {
        return licenseController.create(buildCreateDto(number));
    }

    public LicenseCreateDto buildCreateDto(int number) {
        return LicenseCreateDto.builder()
                .serialNumber("serialNumber" + number)
                .licenseCode("123" + number)
                .build();
    }
}
