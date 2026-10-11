package com.tms.profilemanager.dto.create.limit;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.create.BaseCreateDto;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactLimitCreateDto implements BaseCreateDto<ContactLimitDto> {

    @NotBlank
    @Size(max = 50)
    String name;

    @NotNull
    CurrencyCode currency;

    @NotNull
    Long threshold;

    @NotNull
    Integer targetPercent;

    @NotNull
    Integer maxTargetPercent;

    @NotNull
    Long floorLimit;

    @NotNull
    Boolean pinBypassAllowed;

    @NotNull
    Integer pinLimit;

    @ArraySchema(arraySchema = @Schema(description = "Типы операций, для которых необходимо запрашивать последние четыре цифры PAN"))
    Set<AcquirerOperationType> requestLast4DigitsPAN = new HashSet<>();

}
