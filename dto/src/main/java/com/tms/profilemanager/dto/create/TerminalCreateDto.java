package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.TerminalDto;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.TerminalType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
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
import java.util.ArrayList;
import java.util.List;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class TerminalCreateDto implements BaseCreateDto<TerminalDto> {

    @Getter(AccessLevel.NONE)
    public static final String SERVICE_PIN_PATTERN = "^[0-9]*$";

    @Getter(AccessLevel.NONE)
    public static final String SERVICE_PIN_ERROR_CODE = "{validation.terminal.service-pin.pattern}";


    @NotBlank
    @Size(max = 100)
    @Schema(description = "Серийный номер")
    String serialNumber;

    @NotNull
    @Schema(description = "Модель терминала")
    Long modelId;

    @NotNull
    @Schema(description = "Тип терминала")
    TerminalType defaultTerminalType;

    @NotNull
    @Schema(description = "ID мерчанта (внутренний)")
    Long merchantId;

    @Size(max = 10)
    @Pattern(regexp = SERVICE_PIN_PATTERN, message = SERVICE_PIN_ERROR_CODE)
    String servicePin;

    @Valid
    @ArraySchema(arraySchema = @Schema(description = "Параметры терминала"))
    List<@NotNull TerminalAcquirerParamsInputDto> terminalAcquirerParams;

    @NotBlank
    @Schema(description = "Название страны")
    String country;

    Long groupId;

    @Schema(description = "Адрес")
    @NotBlank
    String address;

    @NotNull
    @Min(value = 0)
    @Max(value = 1)
    Integer status;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @FieldNameConstants
    public static class TerminalAcquirerParamsInputDto {

        @Getter(AccessLevel.NONE)
        public static final String PIN_MASTER_KEY_KCV_PATTERN = "^[0-9A-F]*$";
        @Getter(AccessLevel.NONE)
        public static final String MID_AND_TID_PATTERN = "^[0-9A-Z]*$";
        @Getter(AccessLevel.NONE)
        public static final String PIN_MASTER_KEY_KCV_PATTERN_ERROR_CODE = "{validation.terminal.pin-master-key-kcv.pattern}";
        @Getter(AccessLevel.NONE)
        public static final String TID_PATTERN_ERROR_CODE = "{validation.terminal.tid.pattern}";
        @Getter(AccessLevel.NONE)
        public static final String MID_PATTERN_ERROR_CODE = "{validation.terminal.mid.pattern}";
        @Getter(AccessLevel.NONE)
        public static final String MCC_PATTERN = "^[0-9]{4}$";
        @Getter(AccessLevel.NONE)
        public static final String MCC_PATTERN_ERROR_CODE = "{validation.terminal.mcc.pattern}";

        @NotBlank
        String merchantName;

        @NotNull
        @Size(min = 1, max = 8)
        @Pattern(regexp = MID_AND_TID_PATTERN, message = TID_PATTERN_ERROR_CODE)
        @Schema(description = "ID терминала для ISO транзакций")
        String isoTerminalId;

        @NotNull
        @Size(min = 1, max = 15)
        @Pattern(regexp = MID_AND_TID_PATTERN, message = MID_PATTERN_ERROR_CODE)
        @Schema(description = "ID мерчанта для ISO транзакций")
        String isoMerchantId;

        @Schema(description = "Проверка сохраненного ключа")
        @Size(max = 6)
        @Pattern(regexp = PIN_MASTER_KEY_KCV_PATTERN, message = PIN_MASTER_KEY_KCV_PATTERN_ERROR_CODE)
        String pinMasterKeyKCV;

        @NotNull
        @Schema(description = "Тип терминала")
        TerminalType type;

        @Schema(description = "Код категории мерчанта")
        @NotNull
        @Pattern(regexp = MCC_PATTERN, message = MCC_PATTERN_ERROR_CODE)
        String mcc;

        boolean rkiEnabled;

        @Schema(description = "ID эквайера")
        @NotNull
        Long acquirerId;

        @NotEmpty
        @ArraySchema(arraySchema = @Schema(description = "Перечень доступных операций терминала"))
        List<AcquirerOperationType> availableOperations = new ArrayList<>();

        @ArraySchema(arraySchema = @Schema(description = "Перечень типов платежей"))
        List<String> paramsPaymentTypes;

    }
}
