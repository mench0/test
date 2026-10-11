package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.DRLDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.Optional;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DRLPatchDto extends BasePatchDto<DRLDto> {

    Optional<@NotNull Integer> index;

    Optional<@NotNull Long> operationLimit;

    Optional<@NotNull Long> floorLimit;

    Optional<@NotNull Long> noCvmLimit;

    Optional<Long> contactlessLimitId;
}
