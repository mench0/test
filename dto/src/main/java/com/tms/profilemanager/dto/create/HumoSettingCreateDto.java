package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.HumoSettingDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.tms.profilemanager.dto.create.TerminalCreateDto.SERVICE_PIN_ERROR_CODE;
import static com.tms.profilemanager.dto.create.TerminalCreateDto.SERVICE_PIN_PATTERN;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class HumoSettingCreateDto implements BaseCreateDto<HumoSettingDto> {

    @NotBlank
    @Size(max = 5)
    @Schema(description = "ID в системе Humo")
    String humoId;

    @NotNull
    @Schema(description = "Тип терминала")
    TerminalType terminalType;

    @NotBlank
    @Size(max = 10)
    @Pattern(regexp = SERVICE_PIN_PATTERN, message = SERVICE_PIN_ERROR_CODE)
    @Schema(description = "Сервисный пин")
    String servicePin;

    @NotNull
    @Schema(description = "ID экваера")
    Long acquirerId;

    @NotNull
    @Schema(description = "ID модели")
    Long modelId;

}
