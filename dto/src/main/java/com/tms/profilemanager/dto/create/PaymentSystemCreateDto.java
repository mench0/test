package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.PaymentSystemDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class PaymentSystemCreateDto implements BaseCreateDto<PaymentSystemDto> {

    @NotBlank
    @Size(max = 36)
    String name;

    @NotBlank
    @Size(max = 4)
    String avn;

    @NotBlank
    @Size(max = 4)
    String contactlessAvn;

    @NotEmpty
    @Size(min = 1 , max = 1)
    @ArraySchema(arraySchema = @Schema(description = "Перечень из одного лимита"))
    Set<Long> limits;
}
