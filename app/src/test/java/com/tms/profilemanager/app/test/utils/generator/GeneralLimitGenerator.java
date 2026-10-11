package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.GeneralLimitController;
import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.create.limit.GeneralLimitCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeneralLimitGenerator {

    GeneralLimitController generalLimitController;

    @RunAsSystemUser
    public GeneralLimitDto createAndSave(int number, long contactLimitId, long contactlessLimitId) {
        return generalLimitController.create(buildCreateDto(number, contactLimitId, contactlessLimitId));
    }

    public GeneralLimitCreateDto buildCreateDto(int number, long contactLimitId, long contactlessLimitId) {
        return GeneralLimitCreateDto.builder()
                .name("b" + number)
                .contactLimitId(contactLimitId)
                .contactlessLimitId(contactlessLimitId)
                .build();
    }
}
