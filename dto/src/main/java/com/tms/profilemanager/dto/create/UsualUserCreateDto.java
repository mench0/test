package com.tms.profilemanager.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Locale;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class UsualUserCreateDto extends UserCreateDto {

    @Schema(description = "Email (уникальный)")
    @Email
    @NotBlank
    @Size(max = 50)
    String email;

    @Schema(description = "Настройки пользователя")
    UserSettingsCreateDto userSettings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserSettingsCreateDto {

        @Schema(description = "Язык интерфейса пользователя", allowableValues = { "ru", "en", "uz" }, implementation = String.class)
        Locale locale;
    }
}
