package com.tms.profilemanager.dto.patch;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.Optional;

import static com.tms.profilemanager.dto.create.ReceiptTemplateCreateDto.FONT_SIZE_PATTERN;
import static com.tms.profilemanager.dto.create.ReceiptTemplateCreateDto.FONT_SIZE_PATTERN_ERROR_MESSAGE;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ReceiptTemplatePatchDto {

    @NotNull
    @Schema(description = "ID шаблона")
    Long externalId;

    @NotNull
    @Schema(description = "ID мерчанта")
    Long merchantId;

    @Schema(description = "имя")
    Optional<@NotNull String> name;

    @Valid
    @Schema(description = "Заголовок чека")
    HeaderPatchDto header;

    @Valid
    @Schema(description = "Нижняя часть чека")
    FooterPatchDto footer;

    Optional<@NotNull Boolean> active;

    @Schema(description = "Дата начала действия")
    Optional<@NotNull OffsetDateTime> validFrom;

    @Schema(description = "Дата окончания действия")
    Optional<@NotNull OffsetDateTime> validTo;

    @Data
    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class HeaderPatchDto {
        Optional<@NotBlank String> text;

        Optional<@NotBlank @Pattern(regexp = FONT_SIZE_PATTERN, message = FONT_SIZE_PATTERN_ERROR_MESSAGE) String> fontSize;

        Optional<@NotNull Boolean> italic;

        Optional<@NotNull Boolean> bold;

        Optional<@NotNull Boolean> underline;

        Optional<@NotBlank String> textAlign;
    }

    @Data
    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FooterPatchDto {
        Optional<@NotBlank String> text;

        Optional<@NotBlank @Pattern(regexp = FONT_SIZE_PATTERN, message = FONT_SIZE_PATTERN_ERROR_MESSAGE) String> fontSize;

        Optional<@NotNull Boolean> italic;

        Optional<@NotNull Boolean> bold;

        Optional<@NotNull Boolean> underline;

        Optional<@NotBlank String> textAlign;
    }
}
