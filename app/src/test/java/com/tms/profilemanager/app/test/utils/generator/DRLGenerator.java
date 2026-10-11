package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.DRLController;
import com.tms.profilemanager.dto.DRLDto;
import com.tms.profilemanager.dto.create.DRLCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DRLGenerator {

    DRLController drlController;

    @RunAsSystemUser
    public DRLDto createAndSave(int number, Long contactlessLimitId) {
        return drlController.create(buildCreateDto(number, contactlessLimitId));
    }

    public DRLCreateDto buildCreateDto(int number, Long contactlessLimitId) {
        return DRLCreateDto.builder()
                .index(number)
                .operationLimit((long) number)
                .floorLimit((long) number)
                .noCvmLimit((long) number)
                .contactlessLimitId(contactlessLimitId)
                .build();
    }
}
