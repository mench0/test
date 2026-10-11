package com.tms.profilemanager.app.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.neovisionaries.i18n.CountryCode;
import com.tms.profilemanager.app.config.security.SecurityManager;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.integration.rkl.RKLClient;
import com.tms.profilemanager.app.integration.rkl.dto.RKLResponseDto;
import com.tms.profilemanager.app.mapper.entity.CountryMapper;
import com.tms.profilemanager.app.mapper.entity.LocaleMapper;
import com.tms.profilemanager.app.mapper.entity.TerminalMapper;
import com.tms.profilemanager.app.service.TerminalService;
import com.tms.profilemanager.dto.CountryDto;
import com.tms.profilemanager.dto.LocaleDto;
import com.tms.profilemanager.dto.TerminalDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto;
import com.tms.profilemanager.dto.enums.TerminalStatus;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.enums.sort.TerminalSort;
import com.tms.profilemanager.dto.filter.TerminalFilterDto;
import com.tms.profilemanager.dto.patch.TerminalPatchDto;
import com.tms.profilemanager.dto.request.TerminalEncryptedKeysRequestDto;
import com.tms.profilemanager.dto.request.TerminalEncryptedKeysRequestV2Dto;
import com.tms.profilemanager.dto.request.TerminalSoftwareRequestDto;
import com.tms.profilemanager.dto.response.TerminalEncryptedKeysResponseDto;
import com.tms.profilemanager.dto.view.JsonViews;
import com.tms.profilemanager.dto.view.TerminalView;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api/terminal")
@Tag(name = "Terminals", description = "Терминалы")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TerminalController extends
        AbstractCrudController<Terminal, TerminalDto, TerminalCreateDto, TerminalPatchDto, TerminalFilterDto, TerminalSort> {

    private static final String GET_TERMINAL_EXTENDED_INFO_CIRCUIT_BREAKER = "getTerminalExtendedInfo";

    TerminalService terminalService;
    TerminalMapper terminalMapper;
    CountryMapper countryMapper;
    LocaleMapper localeMapper;
    SecurityManager securityManager;
    RKLClient rklClient;

    public TerminalController(TerminalService service, TerminalMapper mapper, CountryMapper countryMapper, LocaleMapper localeMapper, SecurityManager securityManager, RKLClient rklClient) {
        super(service, mapper);
        this.terminalService = service;
        this.terminalMapper = mapper;
        this.countryMapper = countryMapper;
        this.localeMapper = localeMapper;
        this.securityManager = securityManager;
        this.rklClient = rklClient;
    }

    @PostMapping("/{terminalId}/move-to-merchant/{merchantId}")
    @Operation(summary = "Передать терминал другому мерчанту", description = """
        У старого мерчанта терминал архивируется.

        У нового мерчанта:
        1) если терминала с таким серийным номером нет, то создаем его
        2) если терминал с таким серийным номером есть в архиве, то восстанавливаем его из архива и поля не меняем
        """)
    public TerminalDto moveTerminalToAnotherMerchant(@PathVariable("terminalId") Long terminalId,
                                                     @PathVariable("merchantId") Long merchantId) {
        return mapper.toDto(terminalService.moveTerminalToAnotherMerchant(terminalId, merchantId));
    }

    @GetMapping("/current")
    @Operation(summary = "Получить терминал связанный с текущим системным пользователем")
    public TerminalDto getTerminalLinkedToCurrentSystemUser() {
        return terminalService.getTerminalLinkedToCurrentSystemUser()
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/encrypted-keys")
    @Operation(summary = "Получить зашифрованные секретные ключи терминала из RKL",
            description = "Операцию может выполнить только системный пользователь связанный с терминалом")
    public TerminalEncryptedKeysResponseDto getTerminalEncryptedSecretKeys(@RequestBody TerminalEncryptedKeysRequestDto request) {
        Terminal terminal = terminalService.getTerminalLinkedToCurrentSystemUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // execute rkl request outside transaction because it can take long time
        RKLResponseDto response = rklClient.getRKLResponse(terminal, request);
        terminalService.processRklResponse(terminal, response);
        return terminalMapper.toDtoForTerminal(response);
    }

    @PostMapping("/v2/encrypted-keys")
    @Operation(summary = "Получить зашифрованные секретные ключи терминала из RKL",
            description = "Операцию может выполнить только системный пользователь связанный с терминалом")
    public TerminalEncryptedKeysResponseDto getTerminalEncryptedSecretKeysV2(@RequestBody TerminalEncryptedKeysRequestV2Dto request) {
        Terminal terminal = terminalService.getTerminalLinkedToCurrentSystemUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // execute rkl request outside transaction because it can take long time
        RKLResponseDto response = rklClient.getRKLResponseV2(terminal, request);
        terminalService.processRklResponse(terminal, response);
        return terminalMapper.toDtoForTerminal(response);
    }

    @GetMapping("/count/for-merchants")
    public Map<Long, Long> countForMerchants(@RequestParam("merchantId") List<Long> merchantIds) {
        return terminalService.countForMerchants(merchantIds);
    }

    @GetMapping("/current/extended-info")
    @Operation(summary = "Получить расширенную информацию о терминале",
            description = "Операцию может выполнить только системный пользователь связанный с терминалом")
    @JsonView(JsonViews.Extended.class)
    @CircuitBreaker(name = GET_TERMINAL_EXTENDED_INFO_CIRCUIT_BREAKER)
    public ResponseEntity<TerminalView> getTerminalExtendedInfo() {
        return terminalService.getTerminalLinkedToCurrentSystemUser()
                .map(terminal -> securityManager.runAsSystemUser(() -> terminalMapper.getConfig(terminal)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/types")
    @Operation(summary = "Получить все типы терминалов")
    public Set<TerminalType.Dto> getTerminalTypes() {
        return Arrays.stream(TerminalType.values()).map(TerminalType.Dto::of).collect(toSet());
    }

    @GetMapping("/countries")
    @Operation(summary = "Получить все страны")
    public List<CountryDto> getAllCountries() {
        return countryMapper.toListDto(CountryCode.values());
    }

    @GetMapping("/locales")
    @Operation(summary = "Получить все локали")
    public List<LocaleDto> getAllLocales() {
        return localeMapper.toLocaleDtos();
    }

    @GetMapping("/statuses")
    @Operation(summary = "Получить все статусы терминалов")
    public Set<TerminalStatus.Dto> getTerminalStatuses() {
        return Arrays.stream(TerminalStatus.values()).map(TerminalStatus.Dto::of).collect(toSet());
    }

    @PostMapping("/software-info")
    public ResponseEntity<Void> setTerminalSoftwareVersion(@RequestBody TerminalSoftwareRequestDto dto) {
        var terminal = terminalService.getTerminalLinkedToCurrentSystemUser();

        if (terminal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        securityManager.runAsSystemUser(() ->
                terminalService.updateTerminalSoftwareInfo(terminal.get(), dto)
        );

        return ResponseEntity.noContent().build();
    }
}
