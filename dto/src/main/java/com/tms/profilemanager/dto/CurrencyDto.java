package com.tms.profilemanager.dto;

import com.neovisionaries.i18n.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PROTECTED)
@FieldNameConstants
public class CurrencyDto {

    @Schema(description = "Внутренний системный код валюты")
    String systemCode;

    @Schema(description = "Числовой код валюты")
    Integer numericCode;

    public static CurrencyDto of(CurrencyCode currencyCode) {
        if (currencyCode == null) {
            return null;
        }
        return new CurrencyDto(currencyCode.toString(), currencyCode.getNumeric());
    }
}
