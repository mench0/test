package com.tms.profilemanager.dto;

import com.tms.profilemanager.dto.enums.Permission;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleDto extends BaseDto {

    @Schema(description = "Название роли (уникальное)")
    String name;

    @Schema(description = "Роль всегда имеет полный набор разрешений, " +
            "по умолчанию роль Администратор создается с этим признаком")
    boolean hasAllPermissions;

    @Schema(description = "Роль предназначена для системных пользователей, " +
            "этот признак не влияет на логику работы, он просто справочный для фильтрации")
    boolean systemUserRole;

    @Schema(description = "Роль по умолчанию для терминалов")
    boolean terminalDefaultRole;

    @ArraySchema(arraySchema = @Schema(description = "Набор разрешений, которые входят в роль"))
    Set<Permission> permissions;
}
