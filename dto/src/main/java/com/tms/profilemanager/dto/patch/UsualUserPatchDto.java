package com.tms.profilemanager.dto.patch;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Locale;
import java.util.Optional;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class UsualUserPatchDto extends UserPatchDto {

    @Schema(description = "Email (уникальный)")
    Optional<@NotBlank @Email @Size(max = 50) String> email;

    @Schema(description = "Настройки пользователя")
    UserSettingsPatchDto userSettings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserSettingsPatchDto {

        @Schema(description = "Язык интерфейса пользователя", allowableValues = { "ru", "en", "uz" }, implementation = String.class)
        Optional<Locale> locale;
    }
}
