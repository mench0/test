package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.integration.skapps.dto.merchant.MerchantCreateRequestDto;
import com.tms.profilemanager.app.integration.skapps.dto.merchant.MerchantPatchRequestDto;
import com.tms.profilemanager.app.integration.skapps.dto.terminal.TerminalCreateRequestDto;
import com.tms.profilemanager.app.integration.skapps.dto.terminal.TerminalPatchRequestDto;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.dto.SKAppsMerchantOutboxPayloadDto;
import com.tms.profilemanager.dto.SKAppsTerminalOutboxPayloadDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class SKAppsMapper {
    public abstract MerchantCreateRequestDto toMerchantCreateRequestDto(SKAppsMerchantOutboxPayloadDto skAppsMerchantOutboxPayloadDto);

    public abstract MerchantPatchRequestDto toMerchantPatchRequestDto(SKAppsMerchantOutboxPayloadDto skAppsMerchantOutboxPayloadDto);

    public abstract TerminalCreateRequestDto toTerminalCreateRequestDto(SKAppsTerminalOutboxPayloadDto skAppsTerminalOutboxPayloadDto);

    public abstract TerminalPatchRequestDto toTerminalPatchRequestDto(SKAppsTerminalOutboxPayloadDto skAppsMerchantOutboxPayloadDto);

}
