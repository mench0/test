package com.tms.profilemanager.dto.view;

import com.tms.profilemanager.dto.AcquirerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcquirerView extends AcquirerDto {

    @Schema(description = "Платежные системы")
    Set<PaymentSystemView> paymentSystems;

    @Schema(description = "Бин-диапазоны")
    Set<CardRangeView> cardRanges;
}
