package com.tms.profilemanager.dto.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tms.profilemanager.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseFilterDto<D extends BaseDto, F extends BaseFilterDto<D, F>> {

    @ArraySchema(arraySchema = @Schema(description = "Идентификаторы (ИЛИ)"))
    Set<Long> ids;

    @Schema(description = "Признак архивности (по умолчанию возвращаются только НЕ архивные)")
    @Builder.Default
    Boolean archived = Boolean.FALSE;

    @Schema(description = "Группировка нескольких фильтров через ИЛИ (формат вложенных фильтров такой же как у основного)")
    @JsonProperty("_or_")
    Set<F> subFiltersAggregatedByOr;
}
