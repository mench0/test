package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.audit.AuditHandlerFactory;
import com.tms.profilemanager.app.entity.audit.AuditSection;
import com.tms.profilemanager.app.service.AuditService;
import com.tms.profilemanager.app.test.utils.ExcelUtils;
import com.tms.profilemanager.dto.VendorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class AuditTest extends AbstractIntegrationTest {

    @Value("${security.system-user.default-terminal-password}")
    String defaultTerminalPassword;

    @Autowired
    EntityManager entityManager;
    @Autowired
    AuditHandlerFactory auditHandlerFactory;

    @SpyBean
    AuditService auditService;

    private Long preparedMerchantId;
    private Long preparedPaymentSystemId;
    private Long preparedAcquirerId;
    private Long preparedContactLimitId;
    private Long preparedContactlessLimitId;
    private Long preparedGeneralLimitId;
    private String preparedPaymentTypeValue;
    private Long vendorId;
    private long from;
    private long to;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        from = Timestamp.valueOf(LocalDateTime.now()).getTime();
        preparedMerchantId = merchantGenerator.createAndSave(1).getId();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        preparedPaymentSystemId = paymentSystemGenerator.createAndSave(1, preparedGeneralLimitId).getId();
        preparedAcquirerId = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId)).getId();
        preparedPaymentTypeValue = paymentTypeGenerator.createAndSave(1).getPaymentValue();
        vendorId = vendorGenerator.createAndSave(1).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void upload() throws IOException {
        VendorDto vendorDto = vendorTestClient.getById(vendorId);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        to = Timestamp.valueOf(LocalDateTime.now()).getTime();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        ADMIN,
                        null,
                        List.of(() -> "audit.read")
                )
        );

        byte[] bytes = auditService.exportLogs(List.of(AuditSection.TERMINAL), from, to);
        byte[] templateFile = Files.readAllBytes(Paths.get("src/test/resources/audit-logs-template.xlsx"));
        ExcelUtils.assertExcel(bytes, templateFile);
        SecurityContextHolder.clearContext();
    }
}
