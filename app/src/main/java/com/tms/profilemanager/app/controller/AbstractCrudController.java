package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import com.tms.profilemanager.app.mapper.entity.EntityMapper;
import com.tms.profilemanager.app.service.AbstractCrudService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.create.BaseCreateDto;
import com.tms.profilemanager.dto.enums.sort.AvailableSorts;
import com.tms.profilemanager.dto.error.ErrorResponseDto;
import com.tms.profilemanager.dto.error.ValidationErrorResponseDto;
import com.tms.profilemanager.dto.filter.BaseFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import com.tms.profilemanager.dto.filter.SearchResponse;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Некорректный формат запроса или невалидные данные",
                content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Отсутствуют или неверные учетные данные",
                content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "403", description = "Отсутствует доступ",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Указанный ресурс не найден",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
})
public abstract class AbstractCrudController<E extends BaseEntity, D extends BaseDto, C extends BaseCreateDto<D>, P extends BasePatchDto<D>,
        F extends BaseFilterDto<D, F>, S extends AvailableSorts<D, S>> {

    AbstractCrudService<E, C, P, F> service;
    EntityMapper<E, D> mapper;

    @GetMapping("/{id}")
    @Operation(summary = "Поиск по id")
    public D getById(@PathVariable("id") Long id) {
        try {
            return mapper.toDto(service.getByIdOrElseThrow(id));
        } catch (EntityNotFoundByFieldException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/search")
    @Operation(summary = "Поиск по фильтру с возможностью пагинации и сортировки")
    public SearchResponse<D> search(@RequestBody SearchRequest<F, S> searchRequest) {
        return mapper.toSearchResponse(service.search(searchRequest));
    }

    @PostMapping("/count")
    @Operation(summary = "Получение количества элементов по фильтру")
    public long count(@RequestBody F filterDto) {
        return service.count(filterDto);
    }

    @PostMapping
    @Operation(summary = "Создание")
    public D create(@RequestBody C createDto) {
        return mapper.toDto(service.create(createDto));
    }

    @PatchMapping
    @Operation(summary = "Обновление отдельных полей")
    public D patch(@RequestBody P patchDto) {
        return mapper.toDto(service.patch(patchDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление по id без возможности восстановления")
    public void deleteById(@PathVariable("id") Long id) {
        try {
            service.delete(id);
        } catch (EntityNotFoundByFieldException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/archive")
    @Operation(summary = "Архивирование по id")
    public void archiveById(@PathVariable("id") Long id) {
        try {
            service.archive(id);
        } catch (EntityNotFoundByFieldException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Восстановление из архива по id")
    public void restoreById(@PathVariable("id") Long id) {
        try {
            service.restore(id);
        } catch (EntityNotFoundByFieldException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }
}
