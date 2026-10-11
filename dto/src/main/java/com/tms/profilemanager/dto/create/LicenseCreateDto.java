package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.LicenseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class LicenseCreateDto implements BaseCreateDto<LicenseDto>{

    @Schema(description = "Серийный номер")
    @NotBlank
    @Size(max = 100)
    String serialNumber;

    @Schema(description = "Лицензия")
    @NotBlank
    @Size(max = 100)
    String licenseCode;
}
