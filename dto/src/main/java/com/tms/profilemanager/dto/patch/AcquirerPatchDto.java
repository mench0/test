package com.tms.profilemanager.dto.patch;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.IsoProtocol;
import com.tms.profilemanager.dto.validation.Host;
import com.tms.profilemanager.dto.validation.IpAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

import static com.tms.profilemanager.dto.create.AcquirerCreateDto.RklUrlCreateDto.RKL_PATH_PATTERN;
import static com.tms.profilemanager.dto.create.AcquirerCreateDto.RklUrlCreateDto.RKL_PATH_PATTERN_ERROR_MESSAGE;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class AcquirerPatchDto extends BasePatchDto<AcquirerDto> {

    @Schema(description = "Название (уникальное)")
    Optional<@NotBlank @Size(max = 255) String> name;

    @Schema(description = "Перечень доступных операций (как минимум одна операция должна быть)")
    List<AcquirerOperationType> availableOperations;

    @Schema(description = "Перечень доступных валют (как минимум одна валюта должна быть)")
    SetPatch<CurrencyCode> availableCurrencies;

    @Schema(description = "Признак использования MAC для подписи запросов")
    Optional<@NotNull Boolean> usingMacToSignRequests;

    @Schema(description = "ISO Протокол")
    Optional<@NotNull IsoProtocol> isoProtocol;

    @Valid
    @Schema(description = "Перечень идентификаторов доступных платежных систем (как минимум одна платежная система должна быть)")
    SetPatch<Long> paymentSystemsIds;

    @Valid
    @Schema(description = "Настройки ключей")
    KeySettingsPatchDto keySettings;

    @Valid
    @Schema(description = "Настройки платежного хоста")
    PaymentHostPatchDto paymentHost;

    @Valid
    @Schema(description = "Настройки платежного хоста")
    RklUrlPatchDto rklUrl;

    @Schema(description = "Лимит на выдачу")
    Optional<Long> cashbackLimit;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class KeySettingsPatchDto {

        @Schema(description = "PIN Master Key Index")
        Optional<@NotNull Integer> pinMasterKeyIndex;

        @Schema(description = "PIN Work Key Index")
        Optional<@NotNull Integer> pinWorkKeyIndex;

        @Schema(description = "MAC Master Key Index")
        Optional<Integer> macMasterKeyIndex;

        @Schema(description = "MAC Working Key Index")
        Optional<Integer> macWorkingKeyIndex;

        @Schema(description = "Key scheme")
        Optional<@NotNull AcquirerKeyScheme> keyScheme;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentHostPatchDto {

        @Schema(description = "IP адрес хоста")
        Optional<@NotBlank @IpAddress String> host;

        @Schema(description = "Порт хоста")
        Optional<@NotNull Integer> port;

        @Schema(description = "Признак использования SSL")
        Optional<@NotNull Boolean> useSsl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RklUrlPatchDto {

        @Schema(description = "Признак использования RKL")
        Optional<@NotNull Boolean> rklEnable;

        @Schema(description = "Хост")
        Optional<@Host String> rklHost;

        @Schema(description = "Порт")
        Optional<@Min(1) @Max(65535) Integer> rklPort;

        @Schema(description = "Путь")
        Optional<@Pattern(regexp = RKL_PATH_PATTERN, message = RKL_PATH_PATTERN_ERROR_MESSAGE) String> rklPath;

        @Schema(description = "Признак использования SSL")
        Optional<@NotNull Boolean> rklUseSsl;
    }
}
