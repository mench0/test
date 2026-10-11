package com.tms.profilemanager.dto.create;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.IsoProtocol;
import com.tms.profilemanager.dto.validation.Host;
import com.tms.profilemanager.dto.validation.IpAddress;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class AcquirerCreateDto implements BaseCreateDto<AcquirerDto> {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Название (уникальное)")
    String name;

    @NotEmpty
    @ArraySchema(arraySchema = @Schema(description = "Перечень доступных операций (как минимум одна операция должна быть)"))
    List<AcquirerOperationType> availableOperations;

    @NotEmpty
    @ArraySchema(arraySchema = @Schema(description = "Перечень доступных валют (как минимум одна валюта должна быть)"))
    Set<CurrencyCode> availableCurrencies;

    @NotNull
    @Schema(description = "Признак использования MAC для подписи запросов")
    Boolean usingMacToSignRequests;

    @NotNull
    @Schema(description = "ISO Протокол")
    IsoProtocol isoProtocol;

    @NotEmpty
    @ArraySchema(arraySchema = @Schema(description = "Перечень идентификаторов доступных платежных систем (как минимум одна платежная система должна быть)"))
    Set<Long> paymentSystemsIds;

    @NotNull
    @Valid
    @Schema(description = "Настройки ключей")
    KeySettingsCreateDto keySettings;

    @NotNull
    @Valid
    @Schema(description = "Настройки платежного хоста")
    PaymentHostCreateDto paymentHost;

    @Valid
    @Schema(description = "Настройки платежного хоста")
    RklUrlCreateDto rklUrl;

    @Schema(description = "Лимит на выдачу")
    Long cashbackLimit;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class KeySettingsCreateDto {

        @NotNull
        @Schema(description = "PIN Master Key Index")
        Integer pinMasterKeyIndex;

        @NotNull
        @Schema(description = "PIN Work Key Index")
        Integer pinWorkKeyIndex;

        @Schema(description = "MAC Master Key Index")
        Integer macMasterKeyIndex;

        @Schema(description = "MAC Working Key Index")
        Integer macWorkingKeyIndex;

        @NotNull
        @Schema(description = "Key scheme")
        AcquirerKeyScheme keyScheme;
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentHostCreateDto {

        @NotBlank
        @Schema(description = "IP адрес хоста")
        @IpAddress
        String host;

        @NotNull
        @Schema(description = "Порт хоста")
        Integer port;

        @NotNull
        @Schema(description = "Признак использования SSL")
        Boolean useSsl;
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RklUrlCreateDto {

        public static final String RKL_PATH_PATTERN = "^/$|^/?[a-zA-Z0-9._-]+(?:/[a-zA-Z0-9._-]+)*/?$";
        public static final String RKL_PATH_PATTERN_ERROR_MESSAGE = "{validation.acquirer.rkl.path.pattern}";

        @NotNull
        @Schema(description = "Признак использования RKL")
        Boolean rklEnable;

        @Host
        @Schema(description = "Хост")
        String rklHost;

        @Min(1)
        @Max(65535)
        @Schema(description = "Порт")
        Integer rklPort;

        @Pattern(regexp = RKL_PATH_PATTERN, message = RKL_PATH_PATTERN_ERROR_MESSAGE)
        @Schema(description = "Путь")
        String rklPath;

        @NotNull
        @Schema(description = "Признак использования SSL")
        Boolean rklUseSsl;
    }
}
