package com.tms.profilemanager.dto.create.dictionary;

import com.tms.profilemanager.dto.CardCodeDto;
import com.tms.profilemanager.dto.create.BaseCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class CardCodeCreateDto implements BaseCreateDto<CardCodeDto> {

    public static final String CODE_PATTERN = "^[A-Z0-9]{2}$";
    public static final String CODE_ERROR_MESSAGE = "{validation.card_type.code.pattern}";

    @NotNull
    @Pattern(regexp = CODE_PATTERN, message = CODE_ERROR_MESSAGE)
    @Schema(description = "Код карты")
    String code;

    @NotNull
    @Schema(description = "ID типа карт")
    Long cardTypeId;
}
