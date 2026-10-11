package com.tms.profilemanager.dto.patch.dictionary;

import com.tms.profilemanager.dto.CardBinRangeDto;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardBinRangePatchDto extends BasePatchDto<CardBinRangeDto> {

    Optional<@NotBlank @Size(min = 1, max = 19) String> startBin;

    Optional<@NotBlank @Size(min = 1, max = 19) String> endBin;

    @Schema(description = "ID тип карт")
    Optional<@NotNull Long> cardTypeId;
}
