package com.tms.profilemanager.dto.create.limit;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.dto.ContactlessLimitDto;
import com.tms.profilemanager.dto.create.BaseCreateDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactlessLimitCreateDto implements BaseCreateDto<ContactlessLimitDto> {

    @NotBlank
    @Size(max = 50)
    String name;

    @NotNull
    CurrencyCode currency;

    @NotNull
    Long floorLimit;

    @NotNull
    Long noCvmLimit;

    @NotNull
    Long onDeviceCvmLimit;

    @NotNull
    Long noDeviceCvmLimit;

    List<@NotNull Long> drlIdList;

    @NotNull
    @PositiveOrZero
    @Max(999999999999L)
    Long maxAmount;
}
