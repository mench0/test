package com.tms.profilemanager.dto.view;

import com.fasterxml.jackson.annotation.JsonView;
import com.tms.profilemanager.dto.PaymentTypeDto;
import com.tms.profilemanager.dto.TerminalAcquirerParamsDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonView(JsonViews.Extended.class)
public class TerminalAcquirerParamsView extends TerminalAcquirerParamsDto {

    @Schema(description = "Эквайер")
    AcquirerView acquirer;

    @ArraySchema(arraySchema = @Schema(description = "Перечень доступных типов платежей " +
            "(получаем только при наличии PAYMENT операции в одном из доступном экваере)"))
    List<PaymentTypeDto> paymentTypes;
}
