package com.tms.profilemanager.dto.filter;

import com.tms.profilemanager.dto.PaymentSystemDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentSystemFilterDto extends BaseFilterDto<PaymentSystemDto, PaymentSystemFilterDto> {

    @Singular
    @ArraySchema(arraySchema = @Schema(description = "Названия (ИЛИ)"))
    Set<String> names;

    @Singular
    @ArraySchema(arraySchema = @Schema(description = "Идентификаторы эквайеров (ИЛИ)"))
    Set<Long> acquirerIds;
}
