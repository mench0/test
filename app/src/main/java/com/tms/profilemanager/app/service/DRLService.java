package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.DRL;
import com.tms.profilemanager.app.mapper.filter.DRLSearchMapper;
import com.tms.profilemanager.app.repository.DRLRepository;
import com.tms.profilemanager.app.service.provider.DRLProvider;
import com.tms.profilemanager.dto.create.DRLCreateDto;
import com.tms.profilemanager.dto.filter.DRLFilterDto;
import com.tms.profilemanager.dto.patch.DRLPatchDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.tms.profilemanager.dto.enums.Permission.Values.PAYMENT_SYSTEM_WRITE;

@Service
public class DRLService extends
        AbstractCrudService<DRL, DRLCreateDto, DRLPatchDto, DRLFilterDto> {

    public DRLService(DRLProvider entityProvider,
                      DRLRepository repository,
                      DRLSearchMapper searchMapper,
                      ApplicationEventPublisher eventPublisher) {
        super(DRL.class, entityProvider, repository, repository, searchMapper, eventPublisher);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_WRITE);
    }
}
