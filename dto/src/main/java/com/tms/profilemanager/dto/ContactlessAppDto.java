package com.tms.profilemanager.dto;

import com.tms.profilemanager.dto.enums.TerminalType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactlessAppDto extends BaseDto {

    String aid;

    String kernelId;

    String kernelConfiguration;

    String terminalCaps;

    String terminalCapsCVMNotRequired;

    String terminalAdditionalCaps;

    String tacDefault;

    String tacOnline;

    String tacDenial;

    Long paymentSystemId;

    @Builder.Default
    Set<String> tags = new HashSet<>();

    TerminalType terminalType;
}
