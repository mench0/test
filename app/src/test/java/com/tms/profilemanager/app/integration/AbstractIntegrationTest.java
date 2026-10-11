package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.ProfileManagerApplication;
import com.tms.profilemanager.app.config.security.SecurityManager;
import com.tms.profilemanager.app.integration.rkl.RKLClient;
import com.tms.profilemanager.app.integration.skapps.client.SKAppsAuthClient;
import com.tms.profilemanager.app.integration.skapps.client.SKAppsClient;
import com.tms.profilemanager.app.repository.AcquirerRepository;
import com.tms.profilemanager.app.repository.CardBinRangeRepository;
import com.tms.profilemanager.app.repository.CardCodeRepository;
import com.tms.profilemanager.app.repository.CardRangeRepository;
import com.tms.profilemanager.app.repository.CardTypeRepository;
import com.tms.profilemanager.app.repository.CertificateRepository;
import com.tms.profilemanager.app.repository.ContactlessAppRepository;
import com.tms.profilemanager.app.repository.EMVAppRepository;
import com.tms.profilemanager.app.repository.GeneralLimitRepository;
import com.tms.profilemanager.app.repository.HumoSettingRepository;
import com.tms.profilemanager.app.repository.PaymentSystemRepository;
import com.tms.profilemanager.app.repository.ReceiptTemplateRepository;
import com.tms.profilemanager.app.repository.SKAppsOutboxRepository;
import com.tms.profilemanager.app.repository.TerminalRepository;
import com.tms.profilemanager.app.repository.VendorModelRepository;
import com.tms.profilemanager.app.repository.VendorRepository;
import com.tms.profilemanager.app.service.TerminalService;
import com.tms.profilemanager.app.test.utils.DatabaseCleaner;
import com.tms.profilemanager.app.test.utils.PostgreSQLContainerInitializer;
import com.tms.profilemanager.app.test.utils.RedisContainerInitializer;
import com.tms.profilemanager.app.test.utils.TestUtilsConfiguration;
import com.tms.profilemanager.app.test.utils.generator.AcquirerGenerator;
import com.tms.profilemanager.app.test.utils.generator.CardBinRangeGenerator;
import com.tms.profilemanager.app.test.utils.generator.CardCodeGenerator;
import com.tms.profilemanager.app.test.utils.generator.CardRangeGenerator;
import com.tms.profilemanager.app.test.utils.generator.CardTypeGenerator;
import com.tms.profilemanager.app.test.utils.generator.CertificateGenerator;
import com.tms.profilemanager.app.test.utils.generator.ContactLimitGenerator;
import com.tms.profilemanager.app.test.utils.generator.ContactlessAppGenerator;
import com.tms.profilemanager.app.test.utils.generator.ContactlessLimitGenerator;
import com.tms.profilemanager.app.test.utils.generator.DRLGenerator;
import com.tms.profilemanager.app.test.utils.generator.EMVAppGenerator;
import com.tms.profilemanager.app.test.utils.generator.GeneralLimitGenerator;
import com.tms.profilemanager.app.test.utils.generator.HumoSettingGenerator;
import com.tms.profilemanager.app.test.utils.generator.LicenseGenerator;
import com.tms.profilemanager.app.test.utils.generator.PaymentScreenLogoGenerator;
import com.tms.profilemanager.app.test.utils.generator.MerchantGenerator;
import com.tms.profilemanager.app.test.utils.generator.ModificationAcquirerGenerator;
import com.tms.profilemanager.app.test.utils.generator.ModificationContactlessAppGenerator;
import com.tms.profilemanager.app.test.utils.generator.ModificationEmvAppGenerator;
import com.tms.profilemanager.app.test.utils.generator.ModificationPaymentSystemGenerator;
import com.tms.profilemanager.app.test.utils.generator.ModificationTerminalGenerator;
import com.tms.profilemanager.app.test.utils.generator.PaymentSystemGenerator;
import com.tms.profilemanager.app.test.utils.generator.PaymentTypeGenerator;
import com.tms.profilemanager.app.test.utils.generator.ReceiptTemplateGenerator;
import com.tms.profilemanager.app.test.utils.generator.RoleGenerator;
import com.tms.profilemanager.app.test.utils.generator.SystemUserGenerator;
import com.tms.profilemanager.app.test.utils.generator.TerminalGenerator;
import com.tms.profilemanager.app.test.utils.generator.TerminalGroupGenerator;
import com.tms.profilemanager.app.test.utils.generator.UsualUserGenerator;
import com.tms.profilemanager.app.test.utils.generator.VendorGenerator;
import com.tms.profilemanager.app.test.utils.request.AcquirerTestClient;
import com.tms.profilemanager.app.test.utils.request.CardBinRangeTestClient;
import com.tms.profilemanager.app.test.utils.request.CardCodeTestClient;
import com.tms.profilemanager.app.test.utils.request.CardRangeTestClient;
import com.tms.profilemanager.app.test.utils.request.CardTypeTestClient;
import com.tms.profilemanager.app.test.utils.request.CertificateTestClient;
import com.tms.profilemanager.app.test.utils.request.ContactLimitTestClient;
import com.tms.profilemanager.app.test.utils.request.ContactlessAppTestClient;
import com.tms.profilemanager.app.test.utils.request.ContactlessLimitTestClient;
import com.tms.profilemanager.app.test.utils.request.DRLTestClient;
import com.tms.profilemanager.app.test.utils.request.EMVAppTestClient;
import com.tms.profilemanager.app.test.utils.request.GeneralLimitTestClient;
import com.tms.profilemanager.app.test.utils.request.HumoSettingTestClient;
import com.tms.profilemanager.app.test.utils.request.LicenseTestClient;
import com.tms.profilemanager.app.test.utils.request.PaymentScreenLogoTestClient;
import com.tms.profilemanager.app.test.utils.request.MerchantTestClient;
import com.tms.profilemanager.app.test.utils.request.ModificationAcquirerTestClient;
import com.tms.profilemanager.app.test.utils.request.ModificationContactlessAppTestClient;
import com.tms.profilemanager.app.test.utils.request.ModificationEmvAppTestClient;
import com.tms.profilemanager.app.test.utils.request.ModificationPaymentSystemTestClient;
import com.tms.profilemanager.app.test.utils.request.ModificationTerminalTestClient;
import com.tms.profilemanager.app.test.utils.request.PaymentSystemTestClient;
import com.tms.profilemanager.app.test.utils.request.PaymentTypeTestClient;
import com.tms.profilemanager.app.test.utils.request.ReceiptTemplateTestClient;
import com.tms.profilemanager.app.test.utils.request.RequestSender;
import com.tms.profilemanager.app.test.utils.request.RoleTestClient;
import com.tms.profilemanager.app.test.utils.request.SystemUserTestClient;
import com.tms.profilemanager.app.test.utils.request.TerminalGroupTestClient;
import com.tms.profilemanager.app.test.utils.request.TerminalTestClient;
import com.tms.profilemanager.app.test.utils.request.UsualUserTestClient;
import com.tms.profilemanager.app.test.utils.request.VendorTestClient;
import com.tms.profilemanager.app.utils.EmailSender;
import com.tms.profilemanager.dto.BaseDto;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(
        classes = {
                ProfileManagerApplication.class,
                TestUtilsConfiguration.class
        },
        initializers = {
                PostgreSQLContainerInitializer.class,
                RedisContainerInitializer.class
        })
public abstract class AbstractIntegrationTest {

    @Autowired ContactLimitTestClient contactLimitTestClient;
    @Autowired ContactlessLimitTestClient contactlessLimitTestClient;
    @Autowired DRLTestClient drlTestClient;
    @Autowired ContactlessAppTestClient contactlessAppTestClient;
    @Autowired PaymentSystemTestClient paymentSystemTestClient;
    @Autowired CertificateTestClient certificateTestClient;
    @Autowired CardRangeTestClient cardRangeTestClient;
    @Autowired EMVAppTestClient emvAppTestClient;
    @Autowired RoleTestClient roleTestClient;
    @Autowired UsualUserTestClient usualUserTestClient;
    @Autowired SystemUserTestClient systemUserTestClient;
    @Autowired AcquirerTestClient acquirerTestClient;
    @Autowired TerminalGroupTestClient terminalGroupTestClient;
    @Autowired MerchantTestClient merchantTestClient;
    @Autowired TerminalTestClient terminalTestClient;
    @Autowired LicenseTestClient licenseTestClient;
    @Autowired VendorTestClient vendorTestClient;
    @Autowired ModificationTerminalTestClient modificationTerminalTestClient;
    @Autowired ModificationAcquirerTestClient modificationAcquirerTestClient;
    @Autowired ModificationPaymentSystemTestClient modificationPaymentSystemTestClient;
    @Autowired ModificationEmvAppTestClient modificationEmvAppTestClient;
    @Autowired ModificationContactlessAppTestClient modificationContactlessAppTestClient;
    @Autowired PaymentTypeTestClient paymentTypeTestClient;
    @Autowired HumoSettingTestClient humoSettingTestClient;
    @Autowired GeneralLimitTestClient generalLimitTestClient;
    @Autowired CardTypeTestClient cardTypeTestClient;
    @Autowired CardCodeTestClient cardCodeTestClient;
    @Autowired CardBinRangeTestClient cardBinRangeTestClient;
    @Autowired ReceiptTemplateTestClient receiptTemplateTestClient;
    @Autowired PaymentScreenLogoTestClient paymentScreenLogoTestClient;

    @Autowired ContactLimitGenerator contactLimitGenerator;
    @Autowired ContactlessLimitGenerator contactlessLimitGenerator;
    @Autowired DRLGenerator drlGenerator;
    @Autowired ContactlessAppGenerator contactlessAppGenerator;
    @Autowired PaymentSystemGenerator paymentSystemGenerator;
    @Autowired CertificateGenerator certificateGenerator;
    @Autowired CardRangeGenerator cardRangeGenerator;
    @Autowired EMVAppGenerator emvAppGenerator;
    @Autowired RoleGenerator roleGenerator;
    @Autowired UsualUserGenerator usualUserGenerator;
    @Autowired SystemUserGenerator systemUserGenerator;
    @Autowired AcquirerGenerator acquirerGenerator;
    @Autowired TerminalGroupGenerator terminalGroupGenerator;
    @Autowired MerchantGenerator merchantGenerator;
    @Autowired TerminalGenerator terminalGenerator;
    @Autowired LicenseGenerator licenseGenerator;
    @Autowired VendorGenerator vendorGenerator;
    @Autowired ModificationTerminalGenerator modificationTerminalGenerator;
    @Autowired ModificationAcquirerGenerator modificationAcquirerGenerator;
    @Autowired ModificationPaymentSystemGenerator modificationPaymentSystemGenerator;
    @Autowired ModificationEmvAppGenerator modificationEmvAppGenerator;
    @Autowired ModificationContactlessAppGenerator modificationContactlessAppGenerator;
    @Autowired PaymentTypeGenerator paymentTypeGenerator;
    @Autowired HumoSettingGenerator humoSettingGenerator;
    @Autowired GeneralLimitGenerator generalLimitGenerator;
    @Autowired CardTypeGenerator cardTypeGenerator;
    @Autowired CardCodeGenerator cardCodeGenerator;
    @Autowired CardBinRangeGenerator cardBinRangeGenerator;
    @Autowired ReceiptTemplateGenerator receiptTemplateGenerator;
    @Autowired PaymentScreenLogoGenerator paymentScreenLogoGenerator;

    @Autowired MockMvc mockMvc;
    @Autowired RequestSender requestSender;
    @Autowired DatabaseCleaner databaseCleaner;
    @Autowired SecurityManager securityManager;

    @MockBean RKLClient rklClient;
    @MockBean SKAppsClient skAppsClient;
    @MockBean SKAppsAuthClient skAppsAuthClient;
    @SpyBean EmailSender emailSender;

    @SpyBean TerminalService terminalService;

    @SpyBean TerminalRepository terminalRepository;
    @SpyBean PaymentSystemRepository paymentSystemRepository;
    @SpyBean AcquirerRepository acquirerRepository;
    @SpyBean CardRangeRepository cardRangeRepository;
    @SpyBean VendorModelRepository vendorModelRepository;
    @SpyBean VendorRepository vendorRepository;
    @SpyBean CertificateRepository certificateRepository;
    @SpyBean EMVAppRepository emvAppRepository;
    @SpyBean ContactlessAppRepository contactlessAppRepository;
    @SpyBean HumoSettingRepository humoSettingRepository;
    @SpyBean GeneralLimitRepository generalLimitRepository;
    @SpyBean CardTypeRepository cardTypeRepository;
    @SpyBean CardBinRangeRepository cardBinRangeRepository;
    @SpyBean CardCodeRepository cardCodeRepository;
    @SpyBean ReceiptTemplateRepository receiptTemplateRepository;
    @SpyBean SKAppsOutboxRepository skAppsOutboxRepository;

    public static final String ADMIN = "admin";

    protected void assertThrowsResponseStatusException(Runnable runnable, HttpStatus errorStatus) {
        try {
            runnable.run();
            throw new AssertionError("ResponseStatusException expected, but not thrown");
        } catch (ResponseStatusException e) {
            assertEquals(errorStatus, e.getStatus());
        }
    }

    protected static <D extends BaseDto> List<Long> mapToIds(List<D> entities) {
        return entities.stream().map(BaseDto::getId).collect(toList());
    }

    protected static <D extends BaseDto> Optional<List<Long>> mapToIds(Optional<List<D>> entities) {
        return entities.map(AbstractIntegrationTest::mapToIds);
    }
}
