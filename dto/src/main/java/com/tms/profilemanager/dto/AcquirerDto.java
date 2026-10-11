package com.tms.profilemanager.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.IsoProtocol;
import com.tms.profilemanager.dto.view.JsonViews;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
@FieldNameConstants
public class AcquirerDto extends BaseDto {

    @Schema(description = "Название (уникальное)")
    String name;

    @Builder.Default
    @ArraySchema(arraySchema = @Schema(description = "Перечень доступных операций"))
    List<AcquirerOperationType> availableOperations = new ArrayList<>();

    @Builder.Default
    @ArraySchema(arraySchema = @Schema(description = "Перечень доступных валют"))
    Set<CurrencyDto> availableCurrencies = new HashSet<>();

    @Schema(description = "Признак использования MAC для подписи запросов")
    Boolean usingMacToSignRequests;

    @Schema(description = "ISO Протокол")
    IsoProtocol.Dto isoProtocol;

    @JsonView(JsonViews.Default.class)
    @ArraySchema(arraySchema = @Schema(description = "Перечень доступных платежных систем"))
    Set<AcquirerPaymentSystemDto> paymentSystemsShortInfo;

    @Schema(description = "Настройки ключей")
    KeySettingsDto keySettings;

    @Schema(description = "Настройки платежного хоста")
    PaymentHostDto paymentHost;

    @Schema(description = "Настройки RKL")
    RklUrlDto rklUrl;

    @Schema(description = "Лимит на выдачу")
    Long cashbackLimit;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AcquirerPaymentSystemDto {

        @Schema(description = "ID платежной системы")
        Long id;

        @Schema(description = "Название платежной системы")
        String name;
    }

    @Data
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class KeySettingsDto {

        @Schema(description = "PIN Master Key Index")
        Integer pinMasterKeyIndex;

        @Schema(description = "PIN Work Key Index")
        Integer pinWorkKeyIndex;

        @Schema(description = "MAC Master Key Index")
        Integer macMasterKeyIndex;

        @Schema(description = "MAC Working Key Index")
        Integer macWorkingKeyIndex;

        @Schema(description = "Key scheme")
        AcquirerKeyScheme keyScheme;
    }

    @Data
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentHostDto {

        @Schema(description = "IP адрес хоста")
        String host;

        @Schema(description = "Порт хоста")
        Integer port;

        @Schema(description = "Признак использования SSL")
        Boolean useSsl;
    }

    @Data
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RklUrlDto {

        @Schema(description = "Признак использования RKL")
        Boolean rklEnable;

        @Schema(description = "IP адрес хоста")
        String rklHost;

        @Schema(description = "Порт")
        Integer rklPort;

        @Schema(description = "Путь")
        String rklPath;

        @Schema(description = "Признак использования SSL")
        Boolean rklUseSsl;
    }
}
