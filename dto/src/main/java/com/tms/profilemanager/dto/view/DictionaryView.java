package com.tms.profilemanager.dto.view;

import com.tms.profilemanager.dto.CardBinRangeDto;
import com.tms.profilemanager.dto.CardCodeDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DictionaryView {

    List<CardCodeDto> cardCodes;

    List<CardBinRangeDto> cardBinRanges;

}
