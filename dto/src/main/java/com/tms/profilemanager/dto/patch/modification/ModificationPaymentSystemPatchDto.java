package com.tms.profilemanager.dto.patch.modification;

import com.tms.profilemanager.dto.modification.ModificationPaymentSystemDto;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;
import java.util.Set;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ModificationPaymentSystemPatchDto extends BasePatchDto<ModificationPaymentSystemDto> {

    Optional<@Size(max = 4) String> avn;

    Optional<@Size(max = 4) String> contactlessAvn;

    @NotNull
    Long paymentSystemId;

    @NotNull
    Long groupId;

    @NotNull
    @Size(min = 1 , max = 1)
    Set<Long> limits;
}
