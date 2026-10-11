package com.tms.profilemanager.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.view.JsonViews;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class TerminalAcquirerParamsDto extends BaseDto {

    @Schema(description = "Название мерчанта")
    String merchantName;

    @Schema(description = "ID терминала для ISO транзакций")
    String isoTerminalId;

    @Schema(description = "ID мерчанта для ISO транзакций")
    String isoMerchantId;

    @Schema(description = "Проверка сохраненного ключа")
    String pinMasterKeyKCV;

    @Schema(description = "Время последнего обновления rkl ключа (в конфигурации не возвращается)")
    OffsetDateTime kcvUpdatedDate;

    boolean rkiEnabled;

    @Schema(description = "Тип терминала")
    TerminalType.Dto type;

    @Schema(description = "Код категории мерчанта")
    String mcc;

    @JsonView(JsonViews.Default.class)
    @Schema(description = "ID эквайера")
    Long acquirerId;

    @Schema(description = "Название эквайера")
    String acquirerName;

    @Builder.Default
    @ArraySchema(arraySchema = @Schema(description = "Перечень доступных операций терминала"))
    List<AcquirerOperationType> availableOperations = new ArrayList<>();

    @JsonView(JsonViews.Default.class)
    @ArraySchema(arraySchema = @Schema(description = "Перечень доступных типов платежей " +
            "(получаем только при наличии PAYMENT операции в одном из доступном экваере)"))
    List<String> paramsPaymentTypes;
}
