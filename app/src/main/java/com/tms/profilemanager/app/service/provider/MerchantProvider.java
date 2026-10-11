package com.tms.profilemanager.app.service.provider;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tms.profilemanager.app.entity.Merchant;
import com.tms.profilemanager.app.exception.InvalidPhoneNumberException;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.dto.create.MerchantCreateDto;
import com.tms.profilemanager.dto.patch.MerchantPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class MerchantProvider extends EntityProvider<Merchant, MerchantCreateDto, MerchantPatchDto> {

    @Lazy
    @Autowired
    PhoneNumberUtil phoneNumberUtil;

    @Override
    @Mapping(target = "phone", ignore = true)
    public abstract void initializeEntity(MerchantCreateDto source, @MappingTarget Merchant target);

    @AfterMapping
    protected void afterInitializeEntity(MerchantCreateDto source, @MappingTarget Merchant target) {
        parsePhoneNumber(source.getPhone(), target);
    }

    @Override
    @Mapping(target = "phone", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(MerchantPatchDto patchDto, @MappingTarget Merchant entity);

    @AfterMapping
    protected void afterPatchEntity(MerchantPatchDto source, @MappingTarget Merchant target) {
        if (source.getPhone() != null) {
            parsePhoneNumber(source.getPhone().get(), target);
        }
    }

    private void parsePhoneNumber(String phone, Merchant target) {
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phone, null);
            if (!phoneNumberUtil.isValidNumber(number)) {
                throw new InvalidPhoneNumberException(phone);
            }
            target.setPhone(phone);
        } catch (NumberParseException e) {
            throw new InvalidPhoneNumberException(phone);
        }
    }

    @Override
    protected Class<Merchant> getEntityClass() {
        return Merchant.class;
    }
}
