package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.Vendor;
import com.tms.profilemanager.app.mapper.filter.VendorSearchMapper;
import com.tms.profilemanager.app.repository.VendorRepository;
import com.tms.profilemanager.app.service.provider.VendorProvider;
import com.tms.profilemanager.dto.create.VendorCreateDto;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.filter.VendorFilterDto;
import com.tms.profilemanager.dto.patch.VendorPatchDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VendorService extends AbstractCrudService<Vendor, VendorCreateDto, VendorPatchDto, VendorFilterDto> {

    protected VendorService(VendorProvider entityProvider,
                            VendorRepository repository,
                            VendorSearchMapper searchMapper,
                            ApplicationEventPublisher eventPublisher) {
        super(Vendor.class, entityProvider, repository, repository, searchMapper, eventPublisher);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(Permission.TERMINAL_WRITE);
    }
}
