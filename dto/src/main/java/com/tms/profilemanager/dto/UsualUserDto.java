package com.tms.profilemanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Locale;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsualUserDto extends UserDto {

    String email;

    @Schema(description = "Настройки пользователя")
    UserSettingsDto userSettings;

    @Override
    public boolean isSystemUser() {
        return false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserSettingsDto {

        @Schema(description = "Язык интерфейса пользователя", allowableValues = { "ru", "en", "uz" }, implementation = String.class)
        Locale locale;
    }
}
