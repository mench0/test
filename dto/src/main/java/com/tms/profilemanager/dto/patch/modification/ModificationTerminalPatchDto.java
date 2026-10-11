package com.tms.profilemanager.dto.patch.modification;

import com.tms.profilemanager.dto.modification.ModificationTerminalDto;
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


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ModificationTerminalPatchDto extends BasePatchDto<ModificationTerminalDto> {

    Optional<@Size(max = 100) String> country;

    Optional<@Size(max = 10) String> servicePin;

    @NotNull
    Long groupId;
}
