package com.tms.profilemanager.dto.create.dictionary;

import com.tms.profilemanager.dto.CardBinRangeDto;
import com.tms.profilemanager.dto.create.BaseCreateDto;
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
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class CardBinRangeCreateDto implements BaseCreateDto<CardBinRangeDto> {


    @NotBlank
    @Size(min = 1, max = 19)
    @Schema(description = "Минимальное число")
    String startBin;

    @NotBlank
    @Size(min = 1, max = 19)
    @Schema(description = "Максимальное число")
    String endBin;

    @NotNull
    @Schema(description = "ID типа карт")
    Long cardTypeId;
}
