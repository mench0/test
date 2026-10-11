package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.service.MinioFileService;
import com.tms.profilemanager.dto.PaymentScreenLogoDto;
import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.UsualUserDto;
import com.tms.profilemanager.dto.create.PaymentScreenLogoCreateDto;
import com.tms.profilemanager.dto.create.TerminalGroupCreateDto;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.patch.PaymentScreenLogoPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PaymentScreenLogoIntegrationTest extends AbstractIntegrationTest {

    String token;
    Long preparedRoleId;

    @MockBean
    MinioFileService minioFileService;

    @BeforeEach
    public void beforeEach() throws Exception {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedRoleId = roleGenerator.createAndSave(1, Permission.getAllPermissions()).getId();
        UsualUserDto userDto = usualUserGenerator.createAndSave(1, preparedRoleId);
        token = paymentScreenLogoTestClient.auth(userDto.getUsername());
        requestSender.setCurrentUser(ADMIN);
        when(minioFileService.uploadToMinio(any(), anyString())).thenReturn(String.valueOf(UUID.randomUUID()));

    }

    @Test
    void upload() {
        PaymentScreenLogoCreateDto paymentScreenLogoCreateDto = paymentScreenLogoGenerator.buildCreateDto(1);
        when(minioFileService.uploadToMinio(any(), anyString())).thenReturn(String.valueOf(UUID.randomUUID()));

        paymentScreenLogoTestClient.upload(createTestFile(), paymentScreenLogoCreateDto, token);

        List<PaymentScreenLogoDto> paymentScreenLogoDtoList = paymentScreenLogoTestClient.getAll(token);
        assertEquals(1, paymentScreenLogoDtoList.size());
        assertEquals("name1", paymentScreenLogoDtoList.get(0).getName());

    }

    @Test
    void update() {
        PaymentScreenLogoCreateDto paymentScreenLogoCreateDto = paymentScreenLogoGenerator.buildCreateDto(1);
        when(minioFileService.uploadToMinio(any(), anyString())).thenReturn(String.valueOf(UUID.randomUUID()));

        paymentScreenLogoTestClient.upload(createTestFile(), paymentScreenLogoCreateDto, token);

        List<PaymentScreenLogoDto> paymentScreenLogoDtoList = paymentScreenLogoTestClient.getAll(token);
        PaymentScreenLogoDto paymentScreenLogoDto = paymentScreenLogoDtoList.get(0);
        paymentScreenLogoTestClient.update(createTestFile(), PaymentScreenLogoPatchDto.builder().name("newName").build(), token, paymentScreenLogoDto.getId());

        List<PaymentScreenLogoDto> newPaymentScreenLogoDtoList = paymentScreenLogoTestClient.getAll(token);
        assertEquals(1, newPaymentScreenLogoDtoList.size());
        assertEquals("newName", newPaymentScreenLogoDtoList.get(0).getName());

        paymentScreenLogoTestClient.delete(token, newPaymentScreenLogoDtoList.get(0).getId());
    }

    @Test
    void delete() {
        PaymentScreenLogoCreateDto paymentScreenLogoCreateDto = paymentScreenLogoGenerator.buildCreateDto(4);
        when(minioFileService.uploadToMinio(any(), anyString())).thenReturn(String.valueOf(UUID.randomUUID()));

        paymentScreenLogoTestClient.upload(createTestFile(), paymentScreenLogoCreateDto, token);
        Long id = paymentScreenLogoTestClient.getAll(token).get(0).getId();

        TerminalGroupDto terminalGroupDto = terminalGroupTestClient.create(TerminalGroupCreateDto.builder()
                .name("name")
                .paymentScreenLogos(List.of(id))
                .build());

        assertEquals("name", terminalGroupDto.getName());
        assertEquals(1, terminalGroupDto.getPaymentScreenLogos().size());
        assertEquals(1, paymentScreenLogoTestClient.getAll(token).size());

        paymentScreenLogoTestClient.delete(token, id);

        assertEquals(0, terminalGroupTestClient.getById(terminalGroupDto.getId()).getPaymentScreenLogos().size());
        assertEquals(0, paymentScreenLogoTestClient.getAll(token).size());
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

}
