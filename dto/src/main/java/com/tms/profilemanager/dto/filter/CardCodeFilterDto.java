package com.tms.profilemanager.dto.filter;

import com.tms.profilemanager.dto.CardCodeDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardCodeFilterDto extends BaseFilterDto<CardCodeDto, CardCodeFilterDto> {

    @ArraySchema(arraySchema = @Schema(description = "Id типов карт"))
    Set<@NotNull Long> cardTypeIds;

}