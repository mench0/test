package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.PaymentSystemController;
import com.tms.profilemanager.dto.PaymentSystemDto;
import com.tms.profilemanager.dto.create.PaymentSystemCreateDto;
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
public class PaymentSystemGenerator {

    PaymentSystemController paymentSystemController;

    @RunAsSystemUser
    public PaymentSystemDto createAndSave(int number, Long limitId) {
        return paymentSystemController.create(buildCreateDto(number, limitId));
    }

    public PaymentSystemCreateDto buildCreateDto(int number, Long limitId) {
        String numberStr = Integer.valueOf(number).toString();
        return PaymentSystemCreateDto.builder()
                .name("name" + number)
                .avn("avn" + number)
                .contactlessAvn(numberStr)
                .limits(Set.of(limitId))
                .build();
    }
}
