package com.tms.profilemanager.dto;

import com.tms.profilemanager.dto.enums.TerminalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EMVAppDto extends BaseDto {

    String aid;

    String terminalCaps;

    String terminalAdditionalCaps;

    String tacDefault;

    String tacOnline;

    String tacDenial;

    String tdol;

    String ddol;

    Long paymentSystemId;

    TerminalType terminalType;
}
