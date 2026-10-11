package com.tms.profilemanager.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.tms.profilemanager.dto.view.JsonViews;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseDto {

    @Schema(description = "Идентификатор")
    Long id;

    @JsonView(JsonViews.Default.class)
    @Schema(description = "Время создания")
    OffsetDateTime createdAt;

    @JsonView(JsonViews.Default.class)
    @Schema(description = "Признак, что сущность заархивирована (аналог логического удаления)")
    Boolean archived;
}
