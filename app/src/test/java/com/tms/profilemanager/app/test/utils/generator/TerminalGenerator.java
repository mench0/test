package com.tms.profilemanager.app.test.utils.generator;

import com.neovisionaries.i18n.CountryCode;
import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.TerminalController;
import com.tms.profilemanager.dto.TerminalDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.tms.profilemanager.dto.enums.AcquirerOperationType.SALE;
import static java.util.stream.Collectors.toList;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TerminalGenerator {

    TerminalController terminalController;

    @RunAsSystemUser
    public TerminalDto createAndSave(int number,
                                     Long merchantId,
                                     Set<Long> acquirersIds,
                                     Long modelId,
                                     String paymentTypeValue) {
        return terminalController.create(buildCreateDto(number, merchantId, acquirersIds, modelId, paymentTypeValue));
    }

    public TerminalCreateDto buildCreateDto(int number,
                                            Long merchantId,
                                            Set<Long> acquirersIds,
                                            Long modelId,
                                            String paymentTypeValue) {
        return TerminalCreateDto.builder()
                .serialNumber("serialNumber" + number)
                .modelId(modelId)
                .defaultTerminalType(TerminalType.ATTENDENT_22)
                .servicePin("12345")
                .terminalAcquirerParams(acquirersIds.stream().map(id -> buildParamsInputDto(number, id, paymentTypeValue)).collect(toList()))
                .merchantId(merchantId)
                .country(CountryCode.UZ.getName())
                .address("12348768")
                .status(1)
                .build();
    }

    public TerminalAcquirerParamsInputDto buildParamsInputDto(int number, Long acquirerId, String paymentTypeValue) {
        String numberStr = String.valueOf(number);
        return TerminalAcquirerParamsInputDto.builder()
                .merchantName(numberStr + "/" + numberStr)
                .isoTerminalId(numberStr)
                .isoMerchantId(numberStr)
                .pinMasterKeyKCV(numberStr)
                .rkiEnabled(true)
                .type(TerminalType.ATTENDENT_11)
                .mcc("0001")
                .acquirerId(acquirerId)
                .availableOperations(List.of(SALE))
                .paramsPaymentTypes(List.of(paymentTypeValue))
                .build();
    }
}
