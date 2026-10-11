package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.PaymentSystemDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;
import java.util.Set;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class PaymentSystemPatchDto extends BasePatchDto<PaymentSystemDto> {

    Optional<@NotBlank @Size(max = 36) String> name;

    Optional<@NotBlank @Size(max = 4) String> avn;

    Optional<@NotBlank @Size(max = 4) String> contactlessAvn;

    @Size(min = 1 , max = 1)
    @ArraySchema(arraySchema = @Schema(description = "Перечень лимитов"))
    Set<Long> limits;

}
