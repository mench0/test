package com.tms.profilemanager.app.integration.skapps.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.profilemanager.app.config.security.SecurityManager;
import com.tms.profilemanager.app.entity.Merchant;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.entity.skapps.SKAppsOutbox;
import com.tms.profilemanager.app.integration.skapps.client.SKAppsClient;
import com.tms.profilemanager.app.mapper.entity.SKAppsMapper;
import com.tms.profilemanager.app.service.MerchantService;
import com.tms.profilemanager.app.service.TerminalService;
import com.tms.profilemanager.dto.SKAppsMerchantOutboxPayloadDto;
import com.tms.profilemanager.dto.SKAppsTerminalOutboxPayloadDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SkAppsService {

    TerminalService terminalService;
    MerchantService merchantService;
    ObjectMapper objectMapper;
    SKAppsMapper skAppsMapper;
    SecurityManager securityManager;
    SKAppsClient skAppsClient;

    @Transactional(propagation = Propagation.NEVER)
    public void synchronizeAllMerchants(List<SKAppsOutbox> outboxList) throws JsonProcessingException {
        for (SKAppsOutbox skAppsOutbox : outboxList) {
            SKAppsMerchantOutboxPayloadDto outboxPayloadDto = objectMapper.readValue(skAppsOutbox.getPayload(), SKAppsMerchantOutboxPayloadDto.class);
            var createdInSkApps = merchantService.merchantExistInSKApps(outboxPayloadDto.getIdentityNumber(), outboxPayloadDto.getMId());
            if (createdInSkApps) {
                merchantService.updateSKAppsMerchant(outboxPayloadDto.getMId(), skAppsMapper.toMerchantPatchRequestDto(outboxPayloadDto), skAppsOutbox.getId());
            } else {
                boolean result = merchantService.createSKAppsMerchant(skAppsMapper.toMerchantCreateRequestDto(outboxPayloadDto), skAppsOutbox.getId());
                if(result) {
                    List<Terminal> terminals = terminalService.findTerminalsByMerchantId(skAppsOutbox.getEntityId());
                    for (Terminal terminal: terminals) {
                        skAppsClient.createTerminal(terminalService.createTerminalCreateRequestDto(terminal));
                    }
                }
            }
        }
    }

    @Transactional(propagation = Propagation.NEVER)
    public void synchronizeAllTerminals(List<SKAppsOutbox> outboxList) throws JsonProcessingException {
        for (SKAppsOutbox skAppsOutbox : outboxList) {
            SKAppsTerminalOutboxPayloadDto requestDto = objectMapper.readValue(skAppsOutbox.getPayload(), SKAppsTerminalOutboxPayloadDto.class);
            Terminal terminal = securityManager.runAsSystemUser(() -> terminalService.getByIdOrElseThrow(Long.valueOf(requestDto.getTId())));
            Merchant merchant = terminal.getMerchant();

            boolean createdMerchantInSkApps = Optional.of(merchant)
                    .filter(m -> m.getInnOrPinfl() != null)
                    .map(m -> merchantService.merchantExistInSKApps(m.getInnOrPinfl(), requestDto.getMId()))
                    .orElse(false);

            if (!createdMerchantInSkApps) {
                if (merchant.getInnOrPinfl() != null && merchant.getPhone() != null && merchant.getEmail() != null) {
                    skAppsClient.createMerchant(merchantService.createMerchantCreateRequestDto(merchant));
                } else {
                    continue;
                }
            }

            boolean terminalExistInSKApps = terminalService.terminalExistInSKApps(requestDto.getSerialNumber(), requestDto.getTId());
            if (terminalExistInSKApps) {
                terminalService.updateSKAppsTerminal(skAppsOutbox.getEntityId(), skAppsMapper.toTerminalPatchRequestDto(requestDto), skAppsOutbox.getId());
            } else {
                terminalService.createSKAppsTerminal(skAppsMapper.toTerminalCreateRequestDto(requestDto), skAppsOutbox.getId());
            }
        }
    }
}
