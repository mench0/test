package com.tms.profilemanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptTemplateDto extends BaseDto {

    @Schema(description = "ID шаблона")
    Long externalId;

    @Schema(description = "ID мерчанта")
    Long merchantId;

    @Schema(description = "имя")
    String name;

    @Valid
    @Schema(description = "Заголовок чека")
    HeaderDto header;

    @Valid
    @Schema(description = "Нижняя часть чека")
    FooterDto footer;

    @Schema(description = "Id Логотипа")
    String logoFileId;

    boolean active;

    @Schema(description = "Дата начала действия")
    OffsetDateTime validFrom;

    @Schema(description = "Дата окончания действия")
    OffsetDateTime validTo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class HeaderDto {
        String text;
        Integer fontSize;
        boolean italic;
        boolean bold;
        boolean underline;
        String textAlign;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FooterDto {
        String text;
        Integer fontSize;
        boolean italic;
        boolean bold;
        boolean underline;
        String textAlign;
    }
}
