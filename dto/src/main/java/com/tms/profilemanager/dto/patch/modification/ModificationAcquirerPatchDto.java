package com.tms.profilemanager.dto.patch.modification;

import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
import com.tms.profilemanager.dto.modification.ModificationAcquirerDto;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ModificationAcquirerPatchDto extends BasePatchDto<ModificationAcquirerDto> {

    @NotNull
    Long groupId;

    @NotNull
    Long acquirerId;

    Optional<Long> cashbackLimit;

    @Valid
    ModificationKeySettingsPatchDto keySettings;

    @Valid
    ModificationPaymentHostPatchDto paymentHost;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ModificationKeySettingsPatchDto {

        Optional<Integer> pinMasterKeyIndex;

        Optional<Integer> pinWorkKeyIndex;

        Optional<Integer> macMasterKeyIndex;

        Optional<Integer> macWorkingKeyIndex;

        Optional<AcquirerKeyScheme> keyScheme;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ModificationPaymentHostPatchDto {

        Optional<String> host;

        Optional<Integer> port;

        Optional<Boolean> useSsl;
    }

}
