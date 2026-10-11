package com.tms.profilemanager.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.view.JsonViews;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
@FieldNameConstants
public class TerminalDto extends BaseDto {

    @Schema(description = "Серийный номер")
    String serialNumber;

    @Schema(description = "Вендор терминала")
    String vendor;

    @Schema(description = "Модель терминала")
    String model;

    @Schema(description = "Тип терминала")
    TerminalType.Dto defaultTerminalType;

    @JsonView(JsonViews.Default.class)
    @ArraySchema(arraySchema = @Schema(description = "Параметры терминала"))
    Set<TerminalAcquirerParamsDto> terminalAcquirerParams;

    @Schema(description = "Информация о мерчанте")
    MerchantDto merchant;

    String servicePin;

    @Schema(description = "Страна")
    CountryDto country;

    @Schema(description = "Группа терминала")
    TerminalGroupShortDto group;

    @Schema(description = "Адрес")
    String address;

    @Schema(description = "Время последнего обновления конфигурации (в конфигурации не возвращается)")
    OffsetDateTime configUpdatedDate;

    Integer status;

    String softwareVersion;

    String softwareName;

    String mode;

    String softwareVendor;

    String softwareModel;

}
