package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.dto.create.ReceiptTemplateCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReceiptTemplateGenerator {

    public ReceiptTemplateCreateDto buildCreateDto(Long externalId, Long merchantId, String name) {
        return ReceiptTemplateCreateDto.builder()
                .externalId(externalId)
                .merchantId(merchantId)
                .name(name)
                .active(true)
                .validFrom(OffsetDateTime.now())
                .validTo(OffsetDateTime.now().plusDays(1))
                .header(ReceiptTemplateCreateDto.HeaderDto.builder()
                        .text("Header")
                        .fontSize("12px")
                        .italic(true)
                        .bold(true)
                        .underline(true)
                        .textAlign("CENTER")
                        .build())
                .footer(ReceiptTemplateCreateDto.FooterDto.builder()
                        .text("Footer")
                        .fontSize("10px")
                        .italic(true)
                        .bold(true)
                        .underline(true)
                        .textAlign("CENTER")
                        .build())
                .build();
    }
}
