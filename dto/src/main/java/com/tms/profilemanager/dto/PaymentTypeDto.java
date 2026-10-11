package com.tms.profilemanager.dto;

import com.neovisionaries.i18n.LanguageCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
@FieldNameConstants
public class PaymentTypeDto extends BaseDto {

    @Schema(description = "Код платежа")
    String paymentValue;

    @Schema(description = "Список названий платежей на разных языках")
    Set<PaymentTypeLocaleDto> localizedNames = new HashSet<>();

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentTypeLocaleDto extends BaseDto {

        @Schema(description = "Язык названия платежа")
        LanguageCode locale;

        @Schema(description = "Название платежа")
        String paymentName;

    }
}

