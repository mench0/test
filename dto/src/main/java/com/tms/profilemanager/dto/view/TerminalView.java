package com.tms.profilemanager.dto.view;

import com.tms.profilemanager.dto.PaymentScreenLogoDto;
import com.tms.profilemanager.dto.PaymentTypeDto;
import com.tms.profilemanager.dto.ReceiptTemplateDto;
import com.tms.profilemanager.dto.TerminalDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class TerminalView extends TerminalDto {

    Set<TerminalAcquirerParamsView> acquirers;

    String license;

    List<PaymentTypeDto> paymentTypes;

    DictionaryView dictionaries;

    ReceiptTemplateDto receiptTemplate;

    List<PaymentScreenLogoDto> paymentScreenLogos;
}
