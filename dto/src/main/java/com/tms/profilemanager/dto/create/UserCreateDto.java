package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public abstract class UserCreateDto implements BaseCreateDto<UserDto> {

    @Getter(AccessLevel.NONE)
    public static final String USERNAME_PATTERN = "[A-Za-z0-9_\\-. ]{3,50}";
    @Getter(AccessLevel.NONE)
    public static final String USERNAME_PATTERN_ERROR_CODE = "{validation.user.username.pattern}";


    @Schema(description = "Уникальное имя пользователя (логин), по умолчанию равно email. "
            + "Должен удовлетворять regexp " + USERNAME_PATTERN, example = "user")
    @NotBlank
    @Pattern(regexp = USERNAME_PATTERN, message = USERNAME_PATTERN_ERROR_CODE)
    String username;

    @Schema(description = "Полное имя пользователя (не уникальное)")
    @NotBlank
    @Size(max = 255)
    String fullName;

    @Schema(description = "Идентификатор роли пользователя")
    @NotNull
    Long roleId;

    @ToString.Exclude
    @Size(max = 50)
    String password;
}
