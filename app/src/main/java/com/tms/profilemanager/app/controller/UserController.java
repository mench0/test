package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import com.tms.profilemanager.app.mapper.entity.UserMapper;
import com.tms.profilemanager.app.service.SystemUserService;
import com.tms.profilemanager.app.service.UserService;
import com.tms.profilemanager.app.service.UsualUserService;
import com.tms.profilemanager.dto.SystemUserDto;
import com.tms.profilemanager.dto.UserDto;
import com.tms.profilemanager.dto.UsualUserDto;
import com.tms.profilemanager.dto.request.ChangeUserPasswordRequestDto;
import com.tms.profilemanager.dto.create.SystemUserCreateDto;
import com.tms.profilemanager.dto.create.UsualUserCreateDto;
import com.tms.profilemanager.dto.enums.sort.UserSort;
import com.tms.profilemanager.dto.error.ErrorResponseDto;
import com.tms.profilemanager.dto.error.ValidationErrorResponseDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import com.tms.profilemanager.dto.filter.SearchResponse;
import com.tms.profilemanager.dto.filter.UserFilterDto;
import com.tms.profilemanager.dto.patch.SystemUserPatchDto;
import com.tms.profilemanager.dto.patch.UsualUserPatchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Users", description = "Пользователи")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
public class UserController {

    UserMapper userMapper;
    UserService userService;
    UsualUserService usualUserService;
    SystemUserService systemUserService;

    @GetMapping("/current")
    @Operation(summary = "Получение текущего пользователя")
    public UserDto getCurrentUser() {
        return userService.getCurrentUser()
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск по id")
    public UserDto getById(@PathVariable("id") Long id) {
        try {
            return userMapper.toDto(userService.getByIdOrElseThrow(id));
        } catch (EntityNotFoundByFieldException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/search")
    @Operation(summary = "Поиск по фильтру с возможностью пагинации и сортировки")
    public SearchResponse<UserDto> search(@RequestBody SearchRequest<UserFilterDto, UserSort> searchRequest) {
        return userMapper.toSearchResponse(userService.search(searchRequest));
    }

    @PostMapping("/count")
    @Operation(summary = "Получение количества пользователей по фильтру")
    public long count(@RequestBody UserFilterDto filterDto) {
        return userService.count(filterDto);
    }

    @PostMapping("/usual")
    @Operation(summary = "Создание обычного пользователя")
    public UsualUserDto create(@RequestBody UsualUserCreateDto createDto) {
        return (UsualUserDto) userMapper.toDto(usualUserService.create(createDto));
    }

    @PostMapping("/system")
    @Operation(summary = "Создание системного пользователя")
    public SystemUserDto create(@RequestBody SystemUserCreateDto createDto) {
        return (SystemUserDto) userMapper.toDto(systemUserService.create(createDto));
    }

    @PatchMapping("/usual")
    @Operation(summary = "Обновление отдельных полей обычного пользователя")
    public UsualUserDto patch(@RequestBody UsualUserPatchDto patchDto) {
        return (UsualUserDto) userMapper.toDto(usualUserService.patch(patchDto));
    }

    @PatchMapping("/system")
    @Operation(summary = "Обновление отдельных полей системного пользователя")
    public SystemUserDto patch(@RequestBody SystemUserPatchDto patchDto) {
        return (SystemUserDto) userMapper.toDto(systemUserService.patch(patchDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление по id без возможности восстановления")
    public void deleteById(@PathVariable("id") Long id) {
        try {
            userService.delete(id);
        } catch (EntityNotFoundByFieldException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/archive")
    @Operation(summary = "Архивирование по id")
    public void archiveById(@PathVariable("id") Long id) {
        try {
            userService.archive(id);
        } catch (EntityNotFoundByFieldException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Восстановление из архива по id")
    public void restoreById(@PathVariable("id") Long id) {
        try {
            userService.restore(id);
        } catch (EntityNotFoundByFieldException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/usual/password/send-reset-link")
    @Operation(summary = "Отправление пользователю на почту сообщения со ссылкой для сброса пароля")
    public void sendPasswordResetLink(@Schema(description = "Email пользователя") @RequestParam(name = "email") String email) {
        usualUserService.sendPasswordResetLink(email);
    }

    @PutMapping("/usual/password/change")
    @Operation(summary = "Изменение пароля")
    public void changePassword(@RequestBody ChangeUserPasswordRequestDto command) {
        usualUserService.changePassword(command);
    }
}
