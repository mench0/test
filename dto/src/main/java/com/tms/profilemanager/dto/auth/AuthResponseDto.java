package com.tms.profilemanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponseDto {

    @Schema(ref = "AccessTokenDto")
    AuthTokenDto accessToken;

    @Schema(ref = "RefreshTokenDto")
    AuthTokenDto refreshToken;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AuthTokenDto {

        @Schema(description = "Токен")
        String value;

        @Schema(description = "Время жизни токена в минутах")
        Integer lifeTimeInMinutes;
    }
}
