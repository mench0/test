package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.config.security.SecurityManager;
import com.tms.profilemanager.app.entity.ReceiptTemplate;
import com.tms.profilemanager.app.mapper.entity.ReceiptTemplateMapper;
import com.tms.profilemanager.app.service.ReceiptTemplateService;
import com.tms.profilemanager.app.service.TerminalService;
import com.tms.profilemanager.dto.ReceiptTemplateDto;
import com.tms.profilemanager.dto.create.ReceiptTemplateCreateDto;
import com.tms.profilemanager.dto.patch.ReceiptTemplatePatchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/receipt-template")
@Tag(name = "ReceiptTemplate", description = "Шаблон чека")
@RequiredArgsConstructor
public class ReceiptTemplateController {

    private final ReceiptTemplateService receiptTemplateService;
    private final TerminalService terminalService;
    private final SecurityManager securityManager;
    private final ReceiptTemplateMapper receiptTemplateMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузка шаблона")
    public ReceiptTemplateDto uploadReceiptTemplate(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("createDto") ReceiptTemplateCreateDto createDto
    ) {

        String logoFileId = receiptTemplateService.uploadToMinio(file);
        try {
            return receiptTemplateMapper.toDto(receiptTemplateService.createReceiptTemplate(createDto, logoFileId));
        } catch (Exception e) {
            receiptTemplateService.removeFileFromMinio(logoFileId);
            throw e;
        }
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Обновление шаблона")
    public ReceiptTemplateDto updateReceiptTemplate(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @Valid @RequestPart("patchDto") ReceiptTemplatePatchDto patchDto
    ) {
        ReceiptTemplate receiptTemplate = receiptTemplateService.findReceiptTemplate(patchDto.getExternalId());
        String logoFileId = receiptTemplate.getLogoFileId();

        if (file != null && !file.isEmpty()) {
            String newLogoFileId = receiptTemplateService.uploadToMinio(file);
            receiptTemplateService.removeFileFromMinio(receiptTemplate.getLogoFileId());
            logoFileId = newLogoFileId;
        }

        return receiptTemplateMapper.toDto(receiptTemplateService.updateReceiptTemplate(patchDto, receiptTemplate, logoFileId));
    }

    @GetMapping(value = "/logo", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Получить логотип для шаблона чека")
    public ResponseEntity<byte[]> getLogoFile() {
        return terminalService.getTerminalLinkedToCurrentSystemUser()
                .map(terminal -> securityManager.runAsSystemUser(() ->
                        receiptTemplateService.downloadFile(terminal.getMerchantId())))
                .map(bytes -> ResponseEntity.ok().body(bytes))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{externalId}/deactivate")
    @Operation(summary = "Деактивация шаблона")
    public void deactivateReceiptTemplate(@PathVariable("externalId") Long externalId) {
        receiptTemplateService.deactivateReceiptTemplate(externalId);
    }

    @PatchMapping("/{externalId}/activate")
    @Operation(summary = "Активация шаблона")
    public void activateReceiptTemplate(@PathVariable("externalId") Long externalId) {
        receiptTemplateService.activateReceiptTemplate(externalId);
    }

}
