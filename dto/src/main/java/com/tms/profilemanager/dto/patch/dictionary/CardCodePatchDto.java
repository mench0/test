package com.tms.profilemanager.dto.patch.dictionary;

import com.tms.profilemanager.dto.CardCodeDto;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Optional;

import static com.tms.profilemanager.dto.create.dictionary.CardCodeCreateDto.CODE_ERROR_MESSAGE;
import static com.tms.profilemanager.dto.create.dictionary.CardCodeCreateDto.CODE_PATTERN;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardCodePatchDto extends BasePatchDto<CardCodeDto> {

    @Schema(description = "Код карты")
    Optional<@NotNull @Pattern(regexp = CODE_PATTERN, message = CODE_ERROR_MESSAGE) String> code;

    @Schema(description = "ID тип карт")
    Optional<@NotNull Long> cardTypeId;
}
