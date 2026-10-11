package com.tms.profilemanager.dto;

import com.neovisionaries.i18n.LanguageCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocaleDto {

    @Schema(description = "Локаль")
    String name;

    @Schema(description = "Код локали")
    LanguageCode code;

    public static LocaleDto of(LanguageCode localeCode) {
        if (localeCode == null) {
            return null;
        }
        return new LocaleDto(localeCode.getName(), localeCode.getAlpha3().getAlpha2());
    }
}