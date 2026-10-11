package com.tms.profilemanager.app.test.utils.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.profilemanager.dto.request.TerminalEncryptedKeysRequestDto;
import com.tms.profilemanager.dto.request.TerminalEncryptedKeysRequestV2Dto;
import com.tms.profilemanager.dto.request.TerminalSoftwareRequestDto;
import com.tms.profilemanager.dto.response.TerminalEncryptedKeysResponseDto;
import com.tms.profilemanager.dto.TerminalDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto;
import com.tms.profilemanager.dto.filter.TerminalFilterDto;
import com.tms.profilemanager.dto.patch.TerminalPatchDto;
import com.tms.profilemanager.dto.view.TerminalView;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
public class TerminalTestClient extends
        CrudTestClient<TerminalDto, TerminalCreateDto, TerminalPatchDto, TerminalFilterDto> {

    public TerminalTestClient(RequestSender requestSender) {
        super(TerminalDto.class, requestSender);
    }

    @Override
    protected TypeReference<Page<TerminalDto>> getPageDtoTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected String getBaseUrl() {
        return "/api/terminal";
    }

    public TerminalDto moveTerminalToAnotherMerchant(Long terminalId, Long merchantId) {
        return requestSender.sendAndExpectOk(
                POST, getBaseUrl() + "/" + terminalId + "/move-to-merchant/" + merchantId, null, TerminalDto.class);
    }

    public TerminalDto getTerminalLinkedWithCurrentUser() {
        return requestSender.sendAndExpectOk(GET, getBaseUrl() + "/current", null, TerminalDto.class);
    }

    public TerminalView getTerminalViewLinkedWithCurrentUser() {
        return requestSender.sendAndExpectOk(GET, getBaseUrl() + "/current/extended-info", null, TerminalView.class);
    }

    public MvcResult getMvcResultForTerminalViewLinkedWithCurrentUser() {
        return requestSender.send(GET, getBaseUrl() + "/current/extended-info", null);
    }

    public TerminalEncryptedKeysResponseDto getTerminalEncryptedSecretKeys(TerminalEncryptedKeysRequestDto request) {
        return requestSender.sendAndExpectOk(POST, getBaseUrl() + "/encrypted-keys", request, TerminalEncryptedKeysResponseDto.class);
    }

    public TerminalEncryptedKeysResponseDto getTerminalEncryptedSecretKeysV2(TerminalEncryptedKeysRequestV2Dto request) {
        return requestSender.sendAndExpectOk(POST, getBaseUrl() + "/v2/encrypted-keys", request, TerminalEncryptedKeysResponseDto.class);
    }

    public Map<Long, Long> getCountForMerchants(Set<Long> merchantIds) {
        String parameters = merchantIds.stream().map(id -> "merchantId=" + id).collect(joining("&"));
        return requestSender.sendAndExpectOk(GET, getBaseUrl() + "/count/for-merchants?" + parameters, null, new TypeReference<>() {
        });
    }

    public MvcResult setTerminalSoftwareInfo(TerminalSoftwareRequestDto request) {
        return requestSender.send(POST, getBaseUrl() + "/software-info", request);
    }
}
