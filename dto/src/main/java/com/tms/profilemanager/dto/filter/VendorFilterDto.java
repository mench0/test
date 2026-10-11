package com.tms.profilemanager.dto.filter;

import com.tms.profilemanager.dto.VendorDto;
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
public class VendorFilterDto extends BaseFilterDto<VendorDto, VendorFilterDto> {

    @ArraySchema(arraySchema = @Schema(description = "Имена (ИЛИ)"))
    Set<String> names;
}
