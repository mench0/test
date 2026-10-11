
package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.dto.create.PaymentScreenLogoCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentScreenLogoGenerator {

    public PaymentScreenLogoCreateDto buildCreateDto(Integer num) {
        return PaymentScreenLogoCreateDto.builder()
                .name("name" + num)
                .build();
    }
}
