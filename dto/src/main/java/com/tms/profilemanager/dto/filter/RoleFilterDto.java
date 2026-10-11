package com.tms.profilemanager.dto.filter;

import com.tms.profilemanager.dto.RoleDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleFilterDto extends BaseFilterDto<RoleDto, RoleFilterDto> {

    @Singular
    @ArraySchema(arraySchema = @Schema(description = "Названия (ИЛИ)"))
    Set<String> names;

    @Schema(description = "Роль предназначена для системных пользователей, " +
            "этот признак не влияет на логику работы, он просто справочный для фильтрации")
    Boolean systemUserRole;
}
