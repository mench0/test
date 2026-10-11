package com.tms.profilemanager.dto.view;

import com.tms.profilemanager.dto.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentSystemView extends BaseDto {

    String name;

    String avn;

    String contactlessAvn;

    @Builder.Default
    DefaultLimitsDto defaultLimits = new DefaultLimitsDto();

    Set<CertificateDto> certificates;

    Set<EMVAppDto> emvApps;

    Set<ContactlessAppDto> contactlessApps;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DefaultLimitsDto {

        ContactLimitDto contactLimit;

        ContactlessLimitDto contactlessLimit;
    }
}
