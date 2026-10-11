package com.tms.profilemanager.dto.filter;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchResponse<T> {

    @ArraySchema(arraySchema = @Schema(description = "Набор сущностей (результат фильтрации, сортировки и пагинации)"))
    List<T> content;

    @Schema(description = "Номер страницы (начинается с 0)")
    Integer pageNumber;

    @Schema(description = "Размер страницы")
    Integer pageSize;

    @Schema(description = "Общее количество сущностей, найденных по заданному фильтру")
    Long totalElements;

    @Schema(description = "Общее количество страниц")
    Integer totalPages;
}
