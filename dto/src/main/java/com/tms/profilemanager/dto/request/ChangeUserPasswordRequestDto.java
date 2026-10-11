package com.tms.profilemanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Команда для смены пароля (нужно указать либо secretToken + newPassword, либо userId + oldPassword + newPassword")
public class ChangeUserPasswordRequestDto {

    @Schema(description = "Идентификатор пользователя, которому нужно сменить пароль")
    Long userId;
    @Schema(description = "Текущий пароль пользователя")
    String oldPassword;

    @Schema(description = "Код, который был отправлен пользователю для сброса пароля")
    String secretToken;

    @Schema(description = "Новый пароль пользователя")
    @NotBlank
    @Size(max = 50)
    String newPassword;
}
