package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.DRLDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DRLCreateDto implements BaseCreateDto<DRLDto> {

    @NotNull
    Integer index;

    @NotNull
    Long operationLimit;

    @NotNull
    Long floorLimit;

    @NotNull
    Long noCvmLimit;

    Long contactlessLimitId;
}
