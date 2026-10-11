package com.tms.profilemanager.dto.filter;

import com.tms.profilemanager.dto.TerminalDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalFilterDto extends BaseFilterDto<TerminalDto, TerminalFilterDto> {

    @ArraySchema(arraySchema = @Schema(description = "Идентификаторы групп терминалов"))
    Set<@NotNull Long> terminalGroupIds;

    @ArraySchema(arraySchema = @Schema(description = "Мерчанты терминалов"))
    Set<@NotNull Long> merchantIds;

    @NotBlank
    @Min(3)
    @Schema(description = "Полнотекстовый поиск по serialNumber")
    String searchSerialNumber;

    @NotBlank
    @Min(3)
    @Schema(description = "Полнотекстовый поиск по tid")
    String searchTid;

    @NotBlank
    @Min(3)
    @Schema(description = "Полнотекстовый поиск по mid")
    String searchMid;
}
