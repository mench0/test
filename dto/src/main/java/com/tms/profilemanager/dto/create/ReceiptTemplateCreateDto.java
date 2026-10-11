package com.tms.profilemanager.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.OffsetDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ReceiptTemplateCreateDto {

    public static final String FONT_SIZE_PATTERN = "^\\d+px$";
    public static final String FONT_SIZE_PATTERN_ERROR_MESSAGE = "{validation.receipt-template.pattern.size}";

    @NotNull
    @Schema(description = "ID шаблона")
    Long externalId;

    @NotNull
    @Schema(description = "ID мерчанта")
    Long merchantId;

    @NotBlank
    @Schema(description = "имя")
    String name;

    @NotNull
    @Valid
    @Schema(description = "Заголовок чека")
    HeaderDto header;

    @NotNull
    @Valid
    @Schema(description = "Нижняя часть чека")
    FooterDto footer;

    boolean active;

    @NotNull
    @Schema(description = "Дата начала действия")
    OffsetDateTime validFrom;

    @NotNull
    @Schema(description = "Дата окончания действия")
    OffsetDateTime validTo;

    @Data
    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class HeaderDto {
        @NotBlank
        String text;
        @NotBlank
        @Pattern(regexp = FONT_SIZE_PATTERN, message = FONT_SIZE_PATTERN_ERROR_MESSAGE)
        String fontSize;
        @NotNull
        Boolean italic;
        @NotNull
        Boolean bold;
        @NotNull
        Boolean underline;
        @NotBlank
        String textAlign;
    }

    @Data
    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FooterDto {
        @NotBlank
        String text;
        @NotBlank
        @Pattern(regexp = FONT_SIZE_PATTERN, message = FONT_SIZE_PATTERN_ERROR_MESSAGE)
        String fontSize;
        @NotNull
        Boolean italic;
        @NotNull
        Boolean bold;
        @NotNull
        Boolean underline;
        @NotBlank
        String textAlign;
    }
}
