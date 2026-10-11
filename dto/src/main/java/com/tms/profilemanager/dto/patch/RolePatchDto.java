package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.RoleDto;
import com.tms.profilemanager.dto.enums.Permission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class RolePatchDto extends BasePatchDto<RoleDto> {

    @Schema(description = "Название роли (уникальное)")
    Optional<@NotBlank @Size(max = 255) String> name;

    @Schema(description = "Роль предназначена для системных пользователей, " +
            "этот признак не влияет на логику работы, он просто справочный для фильтрации")
    Optional<Boolean> systemUserRole;

    @Valid
    @Schema(description = "Набор разрешений, входящих в роль")
    SetPatch<Permission> permissions;
}
