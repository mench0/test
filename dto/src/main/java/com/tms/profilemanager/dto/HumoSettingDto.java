package com.tms.profilemanager.dto;

import com.tms.profilemanager.dto.enums.TerminalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
@FieldNameConstants
public class HumoSettingDto extends BaseDto {

    @Schema(description = "ID в системе Humo")
    String humoId;

    @Schema(description = "Тип терминала")
    TerminalType terminalType;

    @Schema(description = "Сервисный пин")
    String servicePin;

    @Schema(description = "Название модели")
    String model;

    @Schema(description = "Название экваера")
    String acquirer;

}
