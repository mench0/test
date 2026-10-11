package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.app.mapper.filter.ContactlessLimitSearchMapper;
import com.tms.profilemanager.app.repository.ContactlessLimitRepository;
import com.tms.profilemanager.app.service.provider.limit.ContactlessLimitProvider;
import com.tms.profilemanager.dto.create.limit.ContactlessLimitCreateDto;
import com.tms.profilemanager.dto.filter.ContactlessLimitFilterDto;
import com.tms.profilemanager.dto.patch.ContactlessLimitPatchDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.tms.profilemanager.dto.enums.Permission.Values.PAYMENT_SYSTEM_WRITE;

@Service
public class ContactlessLimitService extends
        AbstractCrudService<ContactlessLimit, ContactlessLimitCreateDto, ContactlessLimitPatchDto, ContactlessLimitFilterDto> {

    public ContactlessLimitService(ContactlessLimitProvider entityProvider,
                                   ContactlessLimitRepository repository,
                                   ContactlessLimitSearchMapper searchMapper,
                                   ApplicationEventPublisher eventPublisher) {
        super(ContactlessLimit.class, entityProvider, repository, repository, searchMapper, eventPublisher);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_WRITE);
    }
}
