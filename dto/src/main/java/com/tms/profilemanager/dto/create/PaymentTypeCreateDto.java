package com.tms.profilemanager.dto.create;

import com.neovisionaries.i18n.LanguageCode;
import com.tms.profilemanager.dto.PaymentTypeDto;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class PaymentTypeCreateDto implements BaseCreateDto<PaymentTypeDto> {

    @Getter(AccessLevel.NONE)
    public static final String PAYMENT_TYPE_VALUE_PATTERN = "^\\d{3}$";
    @Getter(AccessLevel.NONE)
    public static final String PAYMENT_TYPE_ERROR_CODE = "{validation.payment-type.pattern}";

    @NotBlank
    @Pattern(regexp = PAYMENT_TYPE_VALUE_PATTERN, message = PAYMENT_TYPE_ERROR_CODE)
    @Schema(description = "Код платежа")
    String paymentValue;

    @Valid
    @NotEmpty
    @ArraySchema(arraySchema = @Schema(description = "Список названий типов платежей на разных языках"))
    Set<@NotNull PaymentTypeLocaleInputDto> localizedNames;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @FieldNameConstants
    public static class PaymentTypeLocaleInputDto {

        @NotBlank
        @Size(max = 255)
        @Schema(description = "Название платежа")
        String paymentName;

        @NotNull
        @Schema(description = "Локаль названия платежа")
        LanguageCode locale;
    }
}
