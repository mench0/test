package com.tms.profilemanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class TerminalSoftwareRequestDto {

    @NotBlank
    @Schema(description = "Версия")
    String softwareVersion;

    @NotBlank
    @Schema(description = "Название ПО")
    String softwareName;

    @Schema(description = "Мод")
    String mode;

    @NotBlank
    @Schema(description = "Вендор")
    String softwareVendor;

    @NotBlank
    @Schema(description = "Модель")
    String softwareModel;
}
