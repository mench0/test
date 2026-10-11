package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.service.AuthService;
import com.tms.profilemanager.dto.auth.AuthRequestDto;
import com.tms.profilemanager.dto.auth.AuthResponseDto;
import com.tms.profilemanager.dto.error.ErrorResponseDto;
import com.tms.profilemanager.dto.error.ValidationErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Auth", description = "Получение access и refresh токенов")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Некорректный формат запроса или невалидные данные",
                content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Отсутствуют или неверные учетные данные",
                content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "404", description = "Указанный ресурс не найден",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
})
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Вход пользователя в систему (получение access и refresh токенов)")
    public AuthResponseDto login(@RequestBody AuthRequestDto request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление access и refresh токенов")
    public AuthResponseDto refresh(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
