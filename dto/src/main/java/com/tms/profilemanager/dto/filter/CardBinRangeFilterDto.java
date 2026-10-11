package com.tms.profilemanager.dto.filter;

import com.tms.profilemanager.dto.CardBinRangeDto;
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
public class CardBinRangeFilterDto extends BaseFilterDto<CardBinRangeDto, CardBinRangeFilterDto> {
}
