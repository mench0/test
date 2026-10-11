package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.VendorDto;
import com.tms.profilemanager.dto.create.VendorCreateDto.VendorModelInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class VendorPatchDto extends BasePatchDto<VendorDto> {

    @Schema(description = "Имя вендора (уникальное)")
    Optional<@Size(min = 1, max = 100) String> name;

    @Valid
    @Schema(description = "Список моделей")
    Set<VendorModelInputDto> models = new HashSet<>();
}
