package com.tms.profilemanager.dto.create.modification;

import com.tms.profilemanager.dto.create.BaseCreateDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.modification.FieldModificationDto;
import com.tms.profilemanager.dto.modification.ModificationContactlessAppDto;
import com.tms.profilemanager.dto.modification.SetModificationDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ModificationContactlessAppCreateDto implements BaseCreateDto<ModificationContactlessAppDto> {

    @NotNull
    Long contactlessAppsId;

    @Size(max = 32)
    String aid;

    @Size(max = 2)
    String kernelId;

    @Size(max = 3)
    String kernelConfiguration;

    @Size(max = 6)
    String terminalCaps;

    @Size(max = 6)
    String terminalCapsCVMNotRequired;

    @Size(max = 10)
    String terminalAdditionalCaps;

    @Size(max = 10)
    String tacDefault;

    @Size(max = 10)
    String tacOnline;

    @Size(max = 10)
    String tacDenial;

    @Valid
    SetModificationDto<String> tags;

    @Valid
    FieldModificationDto<TerminalType> terminalType;

    @NotNull
    Long paymentSystemId;

    @NotNull
    Long groupId;

}
