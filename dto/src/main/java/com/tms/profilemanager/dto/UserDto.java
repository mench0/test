package com.tms.profilemanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(subTypes = { UsualUserDto.class, SystemUserDto.class })
public abstract class UserDto extends BaseDto {

    @Schema(description = "Уникальное имя пользователя (логин)")
    String username;

    @Schema(description = "Полное имя пользователя (не уникальное)")
    String fullName;

    @Schema(description = "Роль пользователя")
    RoleDto role;

    @JsonProperty("isSystemUser")
    @Schema(description = "Признак системного пользователя т.е. пользователя из под которого происходят автоматические системные действия")
    public abstract boolean isSystemUser();
}
