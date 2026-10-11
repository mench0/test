package com.tms.profilemanager.app.test.utils.generator;

import com.neovisionaries.i18n.LanguageCode;
import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.PaymentTypeController;
import com.tms.profilemanager.dto.PaymentTypeDto;
import com.tms.profilemanager.dto.create.PaymentTypeCreateDto;
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
public class PaymentTypeGenerator {

    PaymentTypeController paymentTypeController;

    @RunAsSystemUser
    public PaymentTypeDto createAndSave(int i) {
        return paymentTypeController.create(buildCreateDto(i));
    }

    public PaymentTypeCreateDto buildCreateDto(int i) {
        return PaymentTypeCreateDto.builder()
                .paymentValue("50" + i)
                .localizedNames(Set.of(PaymentTypeCreateDto.PaymentTypeLocaleInputDto.builder()
                        .paymentName("Public Utilities")
                        .locale(LanguageCode.en)
                        .build()))
                .build();
    }
}
