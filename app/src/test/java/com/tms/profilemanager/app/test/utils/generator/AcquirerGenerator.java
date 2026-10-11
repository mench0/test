package com.tms.profilemanager.app.test.utils.generator;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.AcquirerController;
import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.create.AcquirerCreateDto;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.IsoProtocol;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AcquirerGenerator {

    AcquirerController acquirerController;

    @RunAsSystemUser
    public AcquirerDto createAndSave(int number, Set<Long> paymentSystemsIds) {
        return acquirerController.create(buildCreateDto(number, paymentSystemsIds));
    }

    public AcquirerCreateDto buildCreateDto(int number, Set<Long> paymentSystemsIds) {
        return AcquirerCreateDto.builder()
                .name("name" + number)
                .availableCurrencies(Set.of(CurrencyCode.USD))
                .availableOperations(List.of(AcquirerOperationType.SALE))
                .usingMacToSignRequests(true)
                .isoProtocol(IsoProtocol.SMART_VISTA)
                .paymentSystemsIds(paymentSystemsIds)
                .keySettings(AcquirerCreateDto.KeySettingsCreateDto.builder()
                        .pinMasterKeyIndex(1)
                        .pinWorkKeyIndex(2)
                        .macMasterKeyIndex(3)
                        .macWorkingKeyIndex(4)
                        .keyScheme(AcquirerKeyScheme.SMARTVISTA_LOCAL)
                        .build())
                .paymentHost(AcquirerCreateDto.PaymentHostCreateDto.builder()
                        .host("1.1.1.1")
                        .port(8080)
                        .useSsl(true)
                        .build())
                .rklUrl(AcquirerCreateDto.RklUrlCreateDto.builder()
                        .rklEnable(true)
                        .rklHost("2.2.2.2")
                        .rklPort(8081)
                        .rklPath("/ab/cd")
                        .rklUseSsl(true)
                        .build())
                .build();
    }
}
