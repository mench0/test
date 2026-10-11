package com.tms.profilemanager.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.profilemanager.app.entity.ReceiptTemplate;
import com.tms.profilemanager.app.integration.skapps.dto.merchant.MerchantResponseDto;
import com.tms.profilemanager.app.integration.skapps.dto.terminal.TerminalResponseDto;
import com.tms.profilemanager.dto.MerchantDto;
import com.tms.profilemanager.dto.ReceiptTemplateDto;
import com.tms.profilemanager.dto.SystemUserDto;
import com.tms.profilemanager.dto.create.MerchantCreateDto;
import com.tms.profilemanager.dto.create.ReceiptTemplateCreateDto;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.patch.ReceiptTemplatePatchDto;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ReceiptTemplateIntegrationTest extends AbstractIntegrationTest {

    @Value("${security.system-user.default-terminal-password}")
    String defaultTerminalPassword;
    @Value("${minio.skapps.bucket}")
    String minioSkappsBucket;

    @Autowired
    ObjectMapper objectMapper;
    SystemUserDto systemUserDto;
    String tokenSKApps;
    Long roleId;

    @MockBean
    MinioClient minioClient;

    @BeforeEach
    public void beforeEach() throws Exception {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        requestSender.setCurrentUser(ADMIN);
        roleId = roleGenerator.createAndSave(1, Set.of(Permission.RECEIPT_TEMPLATE_WRITE, Permission.MERCHANT_READ, Permission.TERMINAL_READ)).getId();
        systemUserDto = systemUserGenerator.createAndSave(1, roleId);
        tokenSKApps = receiptTemplateTestClient.authSKApps(systemUserDto.getUsername());
    }

    @Test
    public void createFile() {
        ReceiptTemplateCreateDto createDto = receiptTemplateGenerator.buildCreateDto(40L, 40L, "1");
        createAndAssertBadRequest(createDto.toBuilder().externalId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().merchantId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().name(null).build());
        createAndAssertBadRequest(createDto.toBuilder().name("").build());
        createAndAssertBadRequest(createDto.toBuilder().name(" ").build());
        createAndAssertBadRequest(createDto.toBuilder().header(null).build());
        createAndAssertBadRequest(createDto.toBuilder().validTo(null).build());
        createAndAssertBadRequest(createDto.toBuilder().validFrom(null).build());

        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().bold(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().italic(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().underline(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().text(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().text("").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().text(" ").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().fontSize(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().fontSize("").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().fontSize(" ").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().textAlign(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().textAlign("").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().header(createDto.getHeader().toBuilder().textAlign(" ").build()).build());

        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().bold(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().italic(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().underline(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().text(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().text("").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().text(" ").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().fontSize(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().fontSize("").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().fontSize(" ").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().textAlign(null).build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().textAlign("").build()).build());
        createAndAssertBadRequest(createDto.toBuilder().footer(createDto.getFooter().toBuilder().textAlign(" ").build()).build());

        MerchantDto merchantDto = merchantGenerator.createAndSave(9);
        Long merchantId = merchantTestClient.getById(merchantDto.getId()).getId();
        assertEquals(merchantDto.getId(), merchantId);

        createDto.setMerchantId(merchantId);
        ReceiptTemplateDto templateDto = receiptTemplateTestClient.uploadTemplate(
                createTestFile(),
                createDto,
                tokenSKApps
        );

        assertCreateDtoMatchesDto(createDto, templateDto);
        assertNotNull(templateDto);

        removeMinioFile(templateDto.getLogoFileId());
    }

    @Test
    public void patchTest() {
        MerchantDto merchantDto = merchantGenerator.createAndSave(1);
        ReceiptTemplateCreateDto createDto = receiptTemplateGenerator.buildCreateDto(41L, merchantDto.getId(), "2");
        ReceiptTemplateDto receiptTemplateDto = receiptTemplateTestClient.uploadTemplate(createTestFile(), createDto, tokenSKApps);
        ReceiptTemplatePatchDto patchDto = ReceiptTemplatePatchDto.builder()
                .externalId(receiptTemplateDto.getExternalId())
                .merchantId(merchantDto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().externalId(null).build());
        patchAndAssertBadRequest(patchDto.toBuilder().merchantId(null).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().active(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().validTo(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().validFrom(Optional.empty()).build());

        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().text(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().text(Optional.of("")).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().text(Optional.of(" ")).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().bold(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().italic(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().underline(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().fontSize(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().fontSize(Optional.of("")).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().fontSize(Optional.of(" ")).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().textAlign(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().fontSize(Optional.of("")).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .header(ReceiptTemplatePatchDto.HeaderPatchDto.builder().fontSize(Optional.of(" ")).build())
                .build());

        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().text(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().text(Optional.of("")).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().text(Optional.of(" ")).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().bold(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().italic(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().underline(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().fontSize(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().fontSize(Optional.of("")).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().fontSize(Optional.of(" ")).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().textAlign(Optional.empty()).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().fontSize(Optional.of("")).build())
                .build());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .footer(ReceiptTemplatePatchDto.FooterPatchDto.builder().fontSize(Optional.of(" ")).build())
                .build());


        assertTrue(receiptTemplateDto.isActive());
        assertFalse(receiptTemplateTestClient.patchTemplate(patchTestFile(), patchDto.toBuilder().active(Optional.of(false)).build(), tokenSKApps).isActive());
        removeMinioFile(receiptTemplateDto.getLogoFileId());
    }

    @Test
    public void deactivateCheckTemplate() {
        MerchantCreateDto merchantCreateDto = merchantGenerator.buildCreateDto(8);
        MerchantDto merchantDto = merchantTestClient.create(merchantCreateDto);
        assertEquals(merchantDto.getId(), merchantTestClient.getById(merchantDto.getId()).getId());

        ReceiptTemplateCreateDto createDto = receiptTemplateGenerator.buildCreateDto(1L, merchantDto.getId(), "1");

        ReceiptTemplateDto dto = receiptTemplateTestClient.uploadTemplate(createTestFile(), createDto, tokenSKApps);

        verify(receiptTemplateRepository, times(1)).save(any(ReceiptTemplate.class));
        assertTrue(dto.isActive());
        Mockito.reset(receiptTemplateRepository);

        receiptTemplateTestClient.deactivateTemplate(tokenSKApps, dto.getExternalId());

        verify(receiptTemplateRepository, times(1)).save(any(ReceiptTemplate.class));

        Mockito.reset(receiptTemplateRepository);
        receiptTemplateTestClient.activateTemplate(tokenSKApps, dto.getExternalId());

        verify(receiptTemplateRepository, times(1)).save(any(ReceiptTemplate.class));

        removeMinioFile(dto.getLogoFileId());

    }

    private void assertCreateDtoMatchesDto(ReceiptTemplateCreateDto createDto, ReceiptTemplateDto dto) {
        assertEquals(createDto.getName(), dto.getName());
        assertEquals(createDto.getExternalId(), dto.getExternalId());
        assertEquals(createDto.getMerchantId(), dto.getMerchantId());
        assertEquals(createDto.getValidTo(), dto.getValidTo());
        assertEquals(createDto.getValidFrom(), dto.getValidFrom());

        assertEquals(createDto.getHeader().getBold(), dto.getHeader().isBold());
        assertEquals(createDto.getHeader().getItalic(), dto.getHeader().isItalic());
        assertEquals(createDto.getHeader().getUnderline(), dto.getHeader().isUnderline());
        assertEquals(createDto.getHeader().getText(), dto.getHeader().getText());
        assertEquals(Integer.parseInt(createDto.getHeader().getFontSize().replace("px", "")), dto.getHeader().getFontSize());
        assertEquals(createDto.getHeader().getTextAlign(), dto.getHeader().getTextAlign());

        assertEquals(createDto.getFooter().getBold(), dto.getFooter().isBold());
        assertEquals(createDto.getFooter().getItalic(), dto.getFooter().isItalic());
        assertEquals(createDto.getFooter().getUnderline(), dto.getFooter().isUnderline());
        assertEquals(createDto.getFooter().getText(), dto.getFooter().getText());
        assertEquals(Integer.parseInt(createDto.getFooter().getFontSize().replace("px", "")), dto.getFooter().getFontSize());
        assertEquals(createDto.getFooter().getTextAlign(), dto.getFooter().getTextAlign());

    }

    private void createAndAssertBadRequest(ReceiptTemplateCreateDto createDto) {
        assertThrowsResponseStatusException(() -> {
            receiptTemplateTestClient.uploadTemplate(createTestFile(), createDto, tokenSKApps);
        }, HttpStatus.BAD_REQUEST);
    }

    private void patchAndAssertBadRequest(ReceiptTemplatePatchDto patchDto) {
        assertThrowsResponseStatusException(() -> receiptTemplateTestClient.patchTemplate(patchTestFile(), patchDto, tokenSKApps),
                HttpStatus.BAD_REQUEST
        );
    }

    public MockMultipartFile createTestFile() {
        String content = "Hello, this is a test file";
        return new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                content.getBytes()
        );
    }

    public MockMultipartFile patchTestFile() {
        String content = "Hello, this is a patch file";
        return new MockMultipartFile(
                "file",
                "patch.png",
                "image/png",
                content.getBytes()
        );
    }


    public static MerchantResponseDto getSKAppsMerchantResponse() {
        return MerchantResponseDto.builder()
                .code(1)
                .message("Merchant successfully created")
                .data(MerchantResponseDto.DataBlock.builder()
                        .id(12345)
                        .email("merchant@example.com")
                        .identityNumber("30512345678901")
                        .firstName("John")
                        .lastName("Doe")
                        .fullName("John Doe")
                        .roleId(4)
                        .statusId(1)
                        .phoneNumber("+998901234567")
                        .dob("1990-10-01")
                        .description("New merchant created via SKApps API")
                        .updatedAt("2025-12-11T06:15:00")
                        .createdAt("2025-12-11T06:15:00")
                        .emailOther("merchant.secondary@example.com")
                        .merchantNameEn("My Test Merchant")
                        .phoneNumberOther("+37490123456")
                        .mId("1")
                        .address("Tashkent, Amir Temur Avenue 12")
                        .merchantTypeId(2)
                        .build()
                )
                .build();

    }

    public static MerchantResponseDto getSKAppsMerchantResponse(Long merchantId) {
        return MerchantResponseDto.builder()
                .code(1)
                .message("Merchant successfully created")
                .data(MerchantResponseDto.DataBlock.builder()
                        .id(12345)
                        .email("merchant@example.com")
                        .identityNumber("30512345678901")
                        .firstName("John")
                        .lastName("Doe")
                        .fullName("John Doe")
                        .roleId(4)
                        .statusId(1)
                        .phoneNumber("+998901234567")
                        .dob("1990-10-01")
                        .description("New merchant created via SKApps API")
                        .updatedAt("2025-12-11T06:15:00")
                        .createdAt("2025-12-11T06:15:00")
                        .emailOther("merchant.secondary@example.com")
                        .merchantNameEn("My Test Merchant")
                        .phoneNumberOther("+37490123456")
                        .mId(String.valueOf(merchantId))
                        .address("Tashkent, Amir Temur Avenue 12")
                        .merchantTypeId(2)
                        .build()
                )
                .build();

    }

    public static TerminalResponseDto getSKAppsTerminalResponse() {
        return TerminalResponseDto.builder()
                .code(200)
                .message("OK")
                .data(
                        TerminalResponseDto.DataBlock.builder()
                                .id(123)
                                .serialNumber("ABC123456")
                                .tId("1")
                                .vendor("Ingenico")
                                .statusId(1)
                                .softwareVersion("1.0.5")
                                .merchantId(10)
                                .createdAt("2025-01-01T12:00:00")
                                .updatedAt("2025-01-10T09:00:00")
                                .transactionCount(152)
                                .model("A930")
                                .lastTransactionDate("2025-01-11T18:30:00")
                                .status(
                                        TerminalResponseDto.Status.builder()
                                                .id(1)
                                                .hy("Ակտիվ")
                                                .en("Active")
                                                .ru("Активный")
                                                .uz("Faol")
                                                .updatedAt("2025-01-05T08:00:00")
                                                .createdAt("2024-12-01T07:00:00")
                                                .isActive(true)
                                                .deletedAt(null)
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    @SneakyThrows
    private void removeMinioFile(String logoId) {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioSkappsBucket).object(logoId).build());

    }
}
