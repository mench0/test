package com.tms.profilemanager.dto.modification;

import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
@FieldNameConstants
public class ModificationEmvAppDto extends BaseDto {

    Long emvAppsId;

    String aid;

    String terminalCaps;

    String terminalAdditionalCaps;

    String tacDefault;

    String tacOnline;

    String tacDenial;

    String tdol;

    String ddol;

    FieldModificationDto<TerminalType> terminalType;

    Long modificationId;
}
