package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.ModificationAcquirerController;
import com.tms.profilemanager.dto.create.modification.ModificationAcquirerCreateDto;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
import com.tms.profilemanager.dto.modification.ModificationAcquirerDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationAcquirerGenerator {

    ModificationAcquirerController modificationAcquirerController;

    @RunAsSystemUser
    public ModificationAcquirerDto createAndSave(Long acquirerId, Long groupId) {
        return modificationAcquirerController.create(buildCreateDto(acquirerId, groupId));
    }

    public ModificationAcquirerCreateDto buildCreateDto(Long acquirerId, Long groupId) {
        return ModificationAcquirerCreateDto.builder()
                .acquirerId(acquirerId)
                .groupId(groupId)
                .cashbackLimit(10L)
                .keySettings(ModificationAcquirerCreateDto.ModificationKeySettingsCreateDto.builder()
                        .macMasterKeyIndex(10)
                        .macWorkingKeyIndex(null)
                        .pinMasterKeyIndex(12)
                        .pinWorkKeyIndex(13)
                        .keyScheme(AcquirerKeyScheme.HUMO)
                        .build())
                .paymentHost(ModificationAcquirerCreateDto.ModificationPaymentHostCreateDto.builder()
                        .host("10.10.10.10")
                        .useSsl(Boolean.TRUE)
                        .port(9090)
                        .build())
                .build();
    }
}
