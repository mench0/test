package com.tms.profilemanager.dto.filter;

import com.tms.profilemanager.dto.enums.sort.AvailableSorts;
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
public class SearchRequest<F extends BaseFilterDto<?, F>, S extends AvailableSorts<?, S>> {

    @Schema(description = "Фильтр")
    F filter;

    @Schema(description = "Пагинация")
    Pagination pagination;

    @ArraySchema(arraySchema = @Schema(description = "Сортировки"))
    List<Sort<S>> sorts;

    public static <F extends BaseFilterDto<?, F>> SearchRequest<F, ?> of(F filterDto) {
        SearchRequest<F, ?> searchRequest = new SearchRequest<>();
        searchRequest.setFilter(filterDto);
        return searchRequest;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Pagination {

        @Schema(description = "Номер страницы (начинается с 0)")
        int pageNumber;

        @Schema(description = "Размер страницы", defaultValue = "20")
        @Builder.Default
        int pageSize = 20;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Sort<S extends AvailableSorts<?, S>> {

        @Schema(description = "Название сортировки")
        S sortName;

        @Schema(description = "Направление сортировки (ASC - по возрастанию, DESC - по убыванию)", defaultValue = "ASC")
        SortDirection direction = SortDirection.ASC;

        public static <S extends AvailableSorts<?, S>> Sort<S> of(S sort, SortDirection sortDirection) {
            return new Sort<>(sort, sortDirection);
        }
    }

    public enum SortDirection {
        ASC, DESC
    }
}
