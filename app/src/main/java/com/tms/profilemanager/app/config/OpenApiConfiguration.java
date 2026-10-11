package com.tms.profilemanager.app.config;

import com.tms.profilemanager.dto.auth.AuthResponseDto.AuthTokenDto;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

import static io.swagger.v3.core.converter.ModelConverters.getInstance;
import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;
import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
public class OpenApiConfiguration {

    public OpenApiConfiguration(MappingJackson2HttpMessageConverter converter) {
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TMS")
                        .version("1.0.0")
                        .description("""
                                ТМС - это единая система хранения конфигураций терминалов.
                                \n
                                Терминалы при старте или по нажатию кнопки на терминале получают актуальную конфигурацию из ТМС."""))
                .components(new Components()
                        .addSecuritySchemes("Bearer Auth",
                                new SecurityScheme()
                                        .type(HTTP)
                                        .scheme("bearer")
                                        .in(HEADER)
                                        .name(AUTHORIZATION)
                                        .bearerFormat("JWT")
                                        .description("""
                                                Bearer аутентификация с использованием access токена в формате JWT.
                                                \n
                                                Access токен можно получить через эндопоинты
                                                /api/auth/login или /api/auth/refresh.
                                                \n
                                                Его нужно передавать в каждом запросе в header Authorization
                                                с префиксом "Bearer ", т.е. если accessToken = 12345, то header
                                                Authorization должен иметь значение "Bearer 12345".
                                                \n
                                                Для отправки запросов через
                                                swagger достаточно вставить accessToken в поле ниже и swagger
                                                автоматически сформирует правильный header для всех запросов.
                                                """)
                        )
                        .addSchemas("AccessTokenDto", getInstance().readAllAsResolvedSchema(AuthTokenDto.class)
                                .schema.description("""
                                        accessToken содержит информацию о текущем пользователе
                                        и его нужно передавать в каждом запросе на сервер
                                        в header Authorization в формате "Bearer accessToken"
                                        """))
                        .addSchemas("RefreshTokenDto", getInstance().readAllAsResolvedSchema(AuthTokenDto.class)
                                .schema.description("""
                                        refreshToken нужен для получения нового accessToken-а,
                                        после того как его время жизни истечет
                                        """))
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"));
    }

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> {
            Schema<String> localeSchema = new Schema<>();
            localeSchema.setEnum(List.of("en", "ru", "uz"));
            Parameter locale = new Parameter()
                    .in("header")
                    .name("Locale")
                    .required(false)
                    .description("Язык интерфейса, значение по умолчанию указывается в настройках пользователя")
                    .schema(localeSchema);
            return operation.addParametersItem(locale);
        };
    }
}
