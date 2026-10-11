package com.tms.profilemanager.dto.create.dictionary;

import com.tms.profilemanager.dto.CardTypeDto;
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

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class CardTypeCreateDto implements BaseCreateDto<CardTypeDto> {

    @NotNull
    @Schema(description = "Название типа карт")
    String name;

}
