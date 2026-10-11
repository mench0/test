package com.tms.profilemanager.dto.create.modification;

import com.tms.profilemanager.dto.create.BaseCreateDto;
import com.tms.profilemanager.dto.modification.ModificationPaymentSystemDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ModificationPaymentSystemCreateDto implements BaseCreateDto<ModificationPaymentSystemDto> {

    @Size(max = 4)
    String avn;

    @Size(max = 4)
    String contactlessAvn;

    @NotNull
    Long paymentSystemId;

    @NotNull
    Long groupId;

    @NotNull
    @Size(min = 1 , max = 1)
    Set<Long> limits;

}
