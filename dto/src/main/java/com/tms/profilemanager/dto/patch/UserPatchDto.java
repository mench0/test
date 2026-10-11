package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Optional;

import static com.tms.profilemanager.dto.create.UserCreateDto.USERNAME_PATTERN;
import static com.tms.profilemanager.dto.create.UserCreateDto.USERNAME_PATTERN_ERROR_CODE;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public abstract class UserPatchDto extends BasePatchDto<UserDto> {

    @Schema(description = "Уникальное имя пользователя (логин), по умолчанию равно email", example = "user")
    Optional<@NotBlank @Pattern(regexp = USERNAME_PATTERN, message = USERNAME_PATTERN_ERROR_CODE) String> username;

    @Schema(description = "Полное имя пользователя (не уникальное)")
    Optional<@NotBlank @Size(max = 255) String> fullName;

    @Schema(description = "Идентификатор роли пользователя")
    Optional<@NotNull Long> roleId;
}
