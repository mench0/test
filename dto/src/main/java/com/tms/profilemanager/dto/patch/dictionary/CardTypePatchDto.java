package com.tms.profilemanager.dto.patch.dictionary;

import com.tms.profilemanager.dto.CardTypeDto;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardTypePatchDto extends BasePatchDto<CardTypeDto> {

    @Schema(description = "Название тип карт")
    Optional<@NotNull String> name;

}
