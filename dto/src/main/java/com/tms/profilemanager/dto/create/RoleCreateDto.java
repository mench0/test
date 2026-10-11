package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.RoleDto;
import com.tms.profilemanager.dto.enums.Permission;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class RoleCreateDto implements BaseCreateDto<RoleDto> {

    @Schema(description = "Название роли (уникальное)")
    @NotBlank
    @Size(max = 255)
    String name;
    
    @Schema(description = "Роль предназначена для системных пользователей, " +
            "этот признак не влияет на логику работы, он просто справочный для фильтрации")
    boolean systemUserRole;

    @ArraySchema(arraySchema = @Schema(description = "Набор разрешений, которые включает в себя роль"))
    Set<Permission> permissions;
}
