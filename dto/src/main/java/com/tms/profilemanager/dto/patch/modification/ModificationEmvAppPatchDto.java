package com.tms.profilemanager.dto.patch.modification;

import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.modification.FieldModificationDto;
import com.tms.profilemanager.dto.modification.ModificationEmvAppDto;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ModificationEmvAppPatchDto extends BasePatchDto<ModificationEmvAppDto> {

    @NotNull
    Long emvAppsId;

    @NotNull
    Long paymentSystemId;

    @NotNull
    Long groupId;

    Optional<@Size(max = 32) String> aid;

    Optional<@Size(max = 6) String> terminalCaps;

    Optional<@Size(max = 10) String> terminalAdditionalCaps;

    Optional<@Size(max = 10) String> tacDefault;

    Optional<@Size(max = 10) String> tacOnline;

    Optional<@Size(max = 10) String> tacDenial;

    Optional<@Size(max = 256) String> tdol;

    Optional<@Size(max = 256) String> ddol;

    @Valid
    FieldModificationDto<TerminalType> terminalType;
}
