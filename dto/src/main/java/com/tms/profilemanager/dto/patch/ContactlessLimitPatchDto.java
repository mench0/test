package com.tms.profilemanager.dto.patch;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.dto.ContactlessLimitDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactlessLimitPatchDto extends BasePatchDto<ContactlessLimitDto> {

    Optional<@NotBlank String> name;

    Optional<@NotNull CurrencyCode> currency;

    Optional<@NotNull Long> floorLimit;

    Optional<@NotNull Long> noCvmLimit;

    Optional<@NotNull Long> onDeviceCvmLimit;

    Optional<@NotNull Long> noDeviceCvmLimit;

    Optional<List<@NotNull Long>> drlIdList;

    Optional<@NotNull @PositiveOrZero @Max(999999999999L) Long>  maxAmount;
}
