package com.tms.profilemanager.dto.filter;

import com.tms.profilemanager.dto.DRLDto;
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
public class DRLFilterDto extends BaseFilterDto<DRLDto, DRLFilterDto> {

    @ArraySchema(arraySchema = @Schema(description = "Идентификаторы бесконтактных лимитов (ИЛИ)"))
    Set<Long> contactlessLimitIds;
}
