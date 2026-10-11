package com.tms.profilemanager.dto.patch;


import com.tms.profilemanager.dto.HumoSettingDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Optional;

import static com.tms.profilemanager.dto.create.TerminalCreateDto.SERVICE_PIN_ERROR_CODE;
import static com.tms.profilemanager.dto.create.TerminalCreateDto.SERVICE_PIN_PATTERN;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class HumoSettingPatchDto extends BasePatchDto<HumoSettingDto> {

    @Schema(description = "ID в системе Humo")
    Optional<@NotBlank @Size(max = 5) String> humoId;

    @Schema(description = "Тип терминала")
    Optional<@NotNull TerminalType> terminalType;

    @Schema(description = "Сервисный пин")
    Optional<@NotBlank @Size(max = 10) @Pattern(regexp = SERVICE_PIN_PATTERN, message = SERVICE_PIN_ERROR_CODE) String> servicePin;

    @Schema(description = "ID экваера")
    Optional<@NotNull Long> acquirerId;

    @Schema(description = "ID модели")
    Optional<@NotNull Long> modelId;

}
