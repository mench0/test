package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.GeneralLimitDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class GeneralLimitPatchDto extends BasePatchDto<GeneralLimitDto> {

    Optional<@NotBlank @Size(max = 50) String> name;

    Optional<@NotNull Long> contactLimitId;

    Optional<@NotNull Long> contactlessLimitId;
}
