package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.LicenseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class LicensePatchDto extends BasePatchDto<LicenseDto>{

    @Schema(description = "Серийный номер")
    Optional<@NotBlank @Size(max = 100) String> serialNumber;

    @Schema(description = "Лицензия")
    Optional<@NotBlank @Size(max = 100) String> licenseCode;
}
