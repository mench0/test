package com.tms.profilemanager.app.test.utils;

import com.tms.profilemanager.app.config.security.SecurityManager;
import com.tms.profilemanager.app.migration.MigrationExecutor;
import com.tms.profilemanager.app.repository.AcquirerRepository;
import com.tms.profilemanager.app.repository.CardBinRangeRepository;
import com.tms.profilemanager.app.repository.CardCodeRepository;
import com.tms.profilemanager.app.repository.CardRangeRepository;
import com.tms.profilemanager.app.repository.CardTypeRepository;
import com.tms.profilemanager.app.repository.CertificateRepository;
import com.tms.profilemanager.app.repository.ContactLimitRepository;
import com.tms.profilemanager.app.repository.ContactlessAppRepository;
import com.tms.profilemanager.app.repository.ContactlessLimitRepository;
import com.tms.profilemanager.app.repository.DRLRepository;
import com.tms.profilemanager.app.repository.EMVAppRepository;
import com.tms.profilemanager.app.repository.GeneralLimitRepository;
import com.tms.profilemanager.app.repository.HumoSettingRepository;
import com.tms.profilemanager.app.repository.LicenseRepository;
import com.tms.profilemanager.app.repository.MerchantRepository;
import com.tms.profilemanager.app.repository.MigrationExecutionRepository;
import com.tms.profilemanager.app.repository.ModificationAcquirerRepository;
import com.tms.profilemanager.app.repository.ModificationContactlessAppRepository;
import com.tms.profilemanager.app.repository.ModificationEmvAppRepository;
import com.tms.profilemanager.app.repository.ModificationPaymentSystemRepository;
import com.tms.profilemanager.app.repository.ModificationTerminalRepository;
import com.tms.profilemanager.app.repository.PaymentScreenLogoRepository;
import com.tms.profilemanager.app.repository.PaymentSystemRepository;
import com.tms.profilemanager.app.repository.PaymentTypeRepository;
import com.tms.profilemanager.app.repository.ReceiptTemplateRepository;
import com.tms.profilemanager.app.repository.RoleRepository;
import com.tms.profilemanager.app.repository.SKAppsOutboxRepository;
import com.tms.profilemanager.app.repository.TerminalGroupRepository;
import com.tms.profilemanager.app.repository.TerminalRepository;
import com.tms.profilemanager.app.repository.TokenRepository;
import com.tms.profilemanager.app.repository.UserRepository;
import com.tms.profilemanager.app.repository.VendorModelRepository;
import com.tms.profilemanager.app.repository.VendorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseCleaner {

    final GeneralLimitRepository generalLimitRepository;
    final ContactLimitRepository contactLimitRepository;
    final ContactlessLimitRepository contactlessLimitRepository;
    final DRLRepository drlRepository;
    final PaymentSystemRepository paymentSystemRepository;
    final ContactlessAppRepository contactlessAppRepository;
    final CertificateRepository certificateRepository;
    final CardRangeRepository cardRangeRepository;
    final EMVAppRepository emvAppRepository;
    final AcquirerRepository acquirerRepository;
    final TokenRepository tokenRepository;
    final MerchantRepository merchantRepository;
    final TerminalGroupRepository terminalGroupRepository;
    final TerminalRepository terminalRepository;
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final MigrationExecutionRepository migrationExecutionRepository;
    final LicenseRepository licenseRepository;
    final VendorRepository vendorRepository;
    final VendorModelRepository vendorModelRepository;
    final ModificationTerminalRepository modificationTerminalRepository;
    final ModificationAcquirerRepository modificationAcquirerRepository;
    final ModificationPaymentSystemRepository modificationPaymentSystemRepository;
    final ModificationEmvAppRepository modificationEmvAppRepository;
    final ModificationContactlessAppRepository modificationContactlessAppRepository;
    final PaymentTypeRepository paymentTypeRepository;
    final HumoSettingRepository humoSettingRepository;
    final CardTypeRepository cardTypeRepository;
    final CardCodeRepository cardCodeRepository;
    final CardBinRangeRepository cardBinRangeRepository;
    final ReceiptTemplateRepository receiptTemplateRepository;
    final SKAppsOutboxRepository skAppsOutboxRepository;
    final PaymentScreenLogoRepository paymentScreenLogoRepository;

    final MigrationExecutor migrationExecutor;
    final SecurityManager securityManager;
    final CacheManager cacheManager;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void clearDatabaseAndExecuteJavaMigrations() {
        clearDatabase();
        clearCache();
        migrationExecutor.executeMigrations();
    }

    @Transactional
    public void clearDatabase() {
        entityManager.createQuery("delete from TerminalAcquirerParams").executeUpdate();
        tokenRepository.deleteAll();
        modificationPaymentSystemRepository.deleteAll();
        modificationEmvAppRepository.deleteAll();
        modificationContactlessAppRepository.deleteAll();
        modificationTerminalRepository.deleteAll();
        modificationAcquirerRepository.deleteAll();
        generalLimitRepository.deleteAll();
        contactlessLimitRepository.deleteAll();
        contactLimitRepository.deleteAll();
        drlRepository.deleteAll();
        cardRangeRepository.deleteAll();
        emvAppRepository.deleteAll();
        contactlessAppRepository.deleteAll();
        certificateRepository.deleteAll();
        paymentScreenLogoRepository.deleteAll();
        paymentSystemRepository.deleteAll();
        humoSettingRepository.deleteAll();
        acquirerRepository.deleteAll();
        licenseRepository.deleteAll();
        receiptTemplateRepository.deleteAll();
        skAppsOutboxRepository.deleteAll();
        terminalRepository.deleteAll();
        merchantRepository.deleteAll();
        terminalGroupRepository.deleteAll();
        vendorModelRepository.deleteAll();
        vendorRepository.deleteAll();
        paymentTypeRepository.deleteAll();
        cardCodeRepository.deleteAll();
        cardBinRangeRepository.deleteAll();
        cardTypeRepository.deleteAll();

        entityManager.flush();
        entityManager.createQuery("update Role r set r.createdBy = null, r.lastUpdatedBy = null").executeUpdate();
        entityManager.createQuery("update User u set u.createdBy = null, u.lastUpdatedBy = null").executeUpdate();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        migrationExecutionRepository.deleteAll();

        securityManager.clearCurrentSystemUserInfo();
    }

    public void clearCache() {
        for (String cacheName : cacheManager.getCacheNames()) {
            requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }
}
