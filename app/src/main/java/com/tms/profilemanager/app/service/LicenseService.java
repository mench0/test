package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.License;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.entity.event.EntityEvent.OperationType;
import com.tms.profilemanager.app.mapper.filter.LicenseSearchMapper;
import com.tms.profilemanager.app.repository.LicenseRepository;
import com.tms.profilemanager.app.service.provider.LicenseProvider;
import com.tms.profilemanager.dto.create.LicenseCreateDto;
import com.tms.profilemanager.dto.filter.LicenseFilterDto;
import com.tms.profilemanager.dto.patch.LicensePatchDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.tms.profilemanager.dto.enums.Permission.Values.TERMINAL_WRITE;

@Service
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LicenseService extends AbstractCrudService<License, LicenseCreateDto, LicensePatchDto, LicenseFilterDto> {

    LicenseRepository licenseRepository;

    protected LicenseService(LicenseProvider entityProvider,
                             LicenseSearchMapper searchMapper,
                             LicenseRepository repository,
                             ApplicationEventPublisher eventPublisher) {
        super(License.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.licenseRepository = repository;
    }

    @Transactional(readOnly = true)
    public List<License> findAllBySerialNumbers(Set<String> serialNumbers) {
        return licenseRepository.findAllBySerialNumberIn(serialNumbers);
    }

    @Transactional
    public void saveAllLicenses(Set<License> licenses) {
        assertWriteAccess();
        licenseRepository.saveAllAndFlush(licenses).forEach(license ->
                eventPublisher.publishEvent(new EntityEvent<>(License.class, license.getId(), OperationType.UPDATE)));
    }

    @Transactional(readOnly = true)
    public Optional<License> findBySn(String serialNumber) {
        return licenseRepository.findBySerialNumber(serialNumber);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(TERMINAL_WRITE);
    }
}
