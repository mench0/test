package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.CertificateController;
import com.tms.profilemanager.app.utils.ClockUtils;
import com.tms.profilemanager.dto.CertificateDto;
import com.tms.profilemanager.dto.create.CertificateCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CertificateGenerator {

    CertificateController certificateController;

    @RunAsSystemUser
    public CertificateDto createAndSave(int number, Long paymentSystemId) {
        return certificateController.create(buildCreateDto(number, paymentSystemId));
    }

    public CertificateCreateDto buildCreateDto(int number, Long paymentSystemId) {
        String numberStr = Integer.valueOf(number).toString();
        return CertificateCreateDto.builder()
                .rid("rid" + number)
                .index(number)
                .exponent(numberStr)
                .modulus("modulus" + numberStr)
                .hash("hash" + numberStr)
                .validUntil(ClockUtils.now().plusDays(1))
                .paymentSystemId(paymentSystemId)
                .build();
    }
}
