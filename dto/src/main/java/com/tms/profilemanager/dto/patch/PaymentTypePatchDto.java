package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.PaymentTypeDto;
import com.tms.profilemanager.dto.create.PaymentTypeCreateDto.PaymentTypeLocaleInputDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.tms.profilemanager.dto.create.PaymentTypeCreateDto.PAYMENT_TYPE_VALUE_PATTERN;
import static com.tms.profilemanager.dto.create.PaymentTypeCreateDto.PAYMENT_TYPE_ERROR_CODE;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class PaymentTypePatchDto extends BasePatchDto<PaymentTypeDto> {

    @Schema(description = "Код платежа")
    Optional<@NotBlank @Pattern(regexp = PAYMENT_TYPE_VALUE_PATTERN, message = PAYMENT_TYPE_ERROR_CODE) String> paymentValue;

    @Valid
    @ArraySchema(arraySchema = @Schema(description = "Список названий типов платежей на разных языках"))
    Set<PaymentTypeLocaleInputDto> localizedNames = new HashSet<>();

}
