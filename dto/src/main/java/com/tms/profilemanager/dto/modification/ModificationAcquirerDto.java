package com.tms.profilemanager.dto.modification;

import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
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
public class ModificationAcquirerDto extends BaseDto {

    Long cashbackLimit;

    ModificationKeySettingsDto keySettings;

    ModificationPaymentHostDto paymentHost;

    Long groupId;

    Long acquirerId;

    @Data
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ModificationKeySettingsDto {

        Integer pinMasterKeyIndex;

        Integer pinWorkKeyIndex;

        Integer macMasterKeyIndex;

        Integer macWorkingKeyIndex;

        AcquirerKeyScheme keyScheme;
    }

    @Data
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ModificationPaymentHostDto {

        String host;

        Integer port;

        Boolean useSsl;
    }
}
