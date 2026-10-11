package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.TerminalDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
public class TerminalPatchDto extends BasePatchDto<TerminalDto> {

    @Schema(description = "Серийный номер")
    Optional<@NotBlank @Size(max = 100) String> serialNumber;

    @Schema(description = "Модель терминала")
    Optional<@NotNull Long> modelId;

    @Schema(description = "Тип терминала")
    Optional<@NotNull TerminalType> defaultTerminalType;

    Optional<@NotBlank @Size(max = 10) @Pattern(regexp = SERVICE_PIN_PATTERN, message = SERVICE_PIN_ERROR_CODE) String> servicePin;

    @Schema(description = "Название страны")
    Optional<@NotBlank String> country;

    @Schema(description = "ID группы терминалов")
    Optional<Long> groupId;

    @Valid
    @Schema(description = "Параметры терминала")
    ListPatch<TerminalAcquirerParamsInputDto> terminalAcquirerParams;

    @Schema(description = "Адрес")
    Optional<@NotBlank String> address;

    Optional<@NotNull @Min(value = 0) @Max(value = 1) Integer> status;

}
