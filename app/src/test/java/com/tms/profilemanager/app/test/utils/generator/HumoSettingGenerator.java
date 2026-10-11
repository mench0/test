package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.HumoSettingController;
import com.tms.profilemanager.dto.HumoSettingDto;
import com.tms.profilemanager.dto.create.HumoSettingCreateDto;
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
public class HumoSettingGenerator {

    HumoSettingController humoSettingController;

    @RunAsSystemUser
    public HumoSettingDto createAndSave(long modelId, long acquirerId) {
        return humoSettingController.create(buildCreateDto(modelId, acquirerId));
    }

    public HumoSettingCreateDto buildCreateDto(long modelId, long acquirerId) {
        return HumoSettingCreateDto.builder()
                .humoId("ERA93")
                .acquirerId(acquirerId)
                .modelId(modelId)
                .terminalType(TerminalType.ATTENDENT_11)
                .servicePin("12345")
                .build();
    }
}
