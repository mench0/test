package com.tms.profilemanager.dto.create.modification;

import com.tms.profilemanager.dto.create.BaseCreateDto;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
import com.tms.profilemanager.dto.modification.ModificationAcquirerDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ModificationAcquirerCreateDto implements BaseCreateDto<ModificationAcquirerDto> {

    @NotNull
    Long groupId;

    @NotNull
    Long acquirerId;

    Long cashbackLimit;

    @Valid
    ModificationKeySettingsCreateDto keySettings;

    @Valid
    ModificationPaymentHostCreateDto paymentHost;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @FieldNameConstants
    public static class ModificationKeySettingsCreateDto {

        Integer pinMasterKeyIndex;

        Integer pinWorkKeyIndex;

        Integer macMasterKeyIndex;

        Integer macWorkingKeyIndex;

        AcquirerKeyScheme keyScheme;
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @FieldNameConstants
    public static class ModificationPaymentHostCreateDto {

        String host;

        Integer port;

        Boolean useSsl;
    }

}
