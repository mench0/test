package com.tms.profilemanager.app.service.provider;

import com.neovisionaries.i18n.LanguageCode;
import com.tms.profilemanager.app.entity.PaymentType;
import com.tms.profilemanager.app.entity.PaymentTypeLocale;
import com.tms.profilemanager.app.exception.DuplicateException;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.dto.create.PaymentTypeCreateDto;
import com.tms.profilemanager.dto.create.PaymentTypeCreateDto.PaymentTypeLocaleInputDto;
import com.tms.profilemanager.dto.patch.PaymentTypePatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class PaymentTypeProvider extends EntityProvider<PaymentType, PaymentTypeCreateDto, PaymentTypePatchDto> {

    @Lazy
    @Autowired
    PaymentTypeLocaleProvider paymentTypeLocaleProvider;

    @Override
    @Mapping(target = "localizedNames", ignore = true)
    public abstract void initializeEntity(PaymentTypeCreateDto source, @MappingTarget PaymentType target);

    @AfterMapping
    protected void afterInitializeEntity(PaymentTypeCreateDto source, @MappingTarget PaymentType target) {
        if (!source.getLocalizedNames().isEmpty()) {
            toPaymentTypeLocaleSet(source.getLocalizedNames())
                    .forEach(paymentTypeLocale -> {
                        paymentTypeLocale.setPaymentType(target);
                        target.getLocalizedNames().add(paymentTypeLocale);
                    });
        }
    }

    @Override
    @Mapping(target = "localizedNames", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(PaymentTypePatchDto patchDto, @MappingTarget PaymentType entity);

    @AfterMapping
    protected void afterPatchEntity(PaymentTypePatchDto patchDto, @MappingTarget PaymentType entity) {
        if (!patchDto.getLocalizedNames().isEmpty()) {
            Set<LanguageCode> existPaymentTypeLocales = entity.getLocalizedNames().stream()
                    .map(PaymentTypeLocale::getLocale)
                    .collect(toSet());
            deleteModels(patchDto, entity, existPaymentTypeLocales);
            patchPaymentTypeAndPaymentTypeLocale(patchDto, entity, existPaymentTypeLocales);
        }
    }

    private void deleteModels(PaymentTypePatchDto source, PaymentType target, Set<LanguageCode> existPaymentTypeLocales) {
        Set<LanguageCode> patchingLocales = source.getLocalizedNames().stream()
                .map(PaymentTypeLocaleInputDto::getLocale)
                .collect(toSet());
        existPaymentTypeLocales.stream()
                .filter(languageCode -> !patchingLocales.contains(languageCode))
                .forEach(locale ->
                        target.getLocalizedNames().removeIf(paymentTypeLocale -> paymentTypeLocale.getLocale().equals(locale)));
    }

    private void patchPaymentTypeAndPaymentTypeLocale(PaymentTypePatchDto source, PaymentType target, Set<LanguageCode> existPaymentTypeLocales) {
        checkIfExistDuplicateNames(source.getLocalizedNames());
        target.getLocalizedNames().forEach(paymentTypeLocale -> source.getLocalizedNames().stream()
                .filter(inputDto -> paymentTypeLocale.getLocale().equals(inputDto.getLocale()))
                .forEach(inputDto -> paymentTypeLocaleProvider.patch(inputDto, paymentTypeLocale))
        );
        source.getLocalizedNames().stream()
                .filter(inputDto -> !existPaymentTypeLocales.contains(inputDto.getLocale()))
                .forEach(paymentTypeLocaleInputDto -> {
                    PaymentTypeLocale paymentTypeLocale = paymentTypeLocaleProvider.create(paymentTypeLocaleInputDto);
                    paymentTypeLocale.setPaymentType(target);
                    target.getLocalizedNames().add(paymentTypeLocale);
                });
    }

    private void checkIfExistDuplicateNames(Set<PaymentTypeLocaleInputDto> inputDtos) {
        long uniqueNamesCount = inputDtos.stream().map(PaymentTypeLocaleInputDto::getLocale).distinct().count();
        if (uniqueNamesCount < inputDtos.size()) {
            throw new DuplicateException("Locale");
        }
    }

    protected abstract Set<PaymentTypeLocale> toPaymentTypeLocaleSet(Set<PaymentTypeLocaleInputDto> paymentTypeLocale);

    @Override
    protected Class<PaymentType> getEntityClass() {
        return PaymentType.class;
    }
}
