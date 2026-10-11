package com.tms.profilemanager.dto;

import com.neovisionaries.i18n.CountryCode;
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
public class CountryDto {

    @Schema(description = "Название страны")
    String name;

    @Schema(description = "Код страны")
    Integer code;

    public static CountryDto of(CountryCode countryCode) {
        if (countryCode == null) {
            return null;
        }
        return new CountryDto(countryCode.getName(), countryCode.getNumeric());
    }
}
