package com.tms.profilemanager.app.entity.audit;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.AcquirerOperation;
import com.tms.profilemanager.app.entity.CardRange;
import com.tms.profilemanager.app.entity.Certificate;
import com.tms.profilemanager.app.entity.ContactLimit;
import com.tms.profilemanager.app.entity.ContactlessApp;
import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.app.entity.DRL;
import com.tms.profilemanager.app.entity.EMVApp;
import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.entity.License;
import com.tms.profilemanager.app.entity.PaymentScreenLogo;
import com.tms.profilemanager.app.entity.Merchant;
import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.entity.PaymentType;
import com.tms.profilemanager.app.entity.PaymentTypeLocale;
import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.app.entity.SystemUser;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.entity.TerminalAcquirerOperation;
import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.UsualUser;
import com.tms.profilemanager.app.entity.Vendor;
import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.entity.dictionary.CardBinRange;
import com.tms.profilemanager.app.entity.dictionary.CardCode;
import com.tms.profilemanager.app.entity.dictionary.CardType;
import com.tms.profilemanager.app.entity.modification.ModificationAcquirer;
import com.tms.profilemanager.app.entity.modification.ModificationContactlessApp;
import com.tms.profilemanager.app.entity.modification.ModificationEmvApp;
import com.tms.profilemanager.app.entity.modification.ModificationPaymentSystem;
import com.tms.profilemanager.app.entity.modification.ModificationTerminal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AuditSection {

    MERCHANT(Entities.MERCHANT),
    TERMINAL(Entities.TERMINAL),
    GROUPS(Entities.GROUP),
    PAYMENT_SYSTEM(Entities.PAYMENT_SYSTEM),
    LIMIT(Entities.LIMIT),
    ACQUIRER(Entities.ACQUIRER),
    USER(Entities.USER),
    ROLE(Entities.ROLE),
    VENDOR(Entities.VENDOR),
    PAYMENT_TYPE(Entities.PAYMENT_TYPE),
    HUMO_SETTING(Entities.HUMO_SETTING),
    CARD_TYPE(Entities.DICTIONARY);

    List<Class<?>> entityClasses;

    private static class Entities {
        static final List<Class<?>> MERCHANT = List.of(
                Merchant.class
        );
        static final List<Class<?>> TERMINAL = List.of(
                Terminal.class,
                TerminalAcquirerParams.class,
                TerminalAcquirerOperation.class,
                License.class
        );
        static final List<Class<?>> GROUP = List.of(
                TerminalGroup.class,
                ModificationTerminal.class,
                ModificationAcquirer.class,
                ModificationPaymentSystem.class,
                ModificationEmvApp.class,
                ModificationContactlessApp.class
        );
        static final List<Class<?>> PAYMENT_SYSTEM = List.of(
                PaymentSystem.class,
                Certificate.class,
                ContactlessApp.class,
                EMVApp.class,
                PaymentScreenLogo.class
        );
        static final List<Class<?>> LIMIT = List.of(
                GeneralLimit.class,
                ContactLimit.class,
                ContactlessLimit.class,
                DRL.class

        );
        static final List<Class<?>> ACQUIRER = List.of(
                Acquirer.class,
                AcquirerOperation.class,
                CardRange.class
        );
        static final List<Class<?>> USER = List.of(
                UsualUser.class,
                SystemUser.class
        );
        static final List<Class<?>> ROLE = List.of(
                Role.class
        );
        static final List<Class<?>> VENDOR = List.of(
                Vendor.class,
                VendorModel.class
        );
        static final List<Class<?>> PAYMENT_TYPE = List.of(
                PaymentType.class,
                PaymentTypeLocale.class
        );
        static final List<Class<?>> HUMO_SETTING = List.of(
                HumoSetting.class
        );
        static final List<Class<?>> DICTIONARY = List.of(
                CardCode.class, CardBinRange.class, CardType.class
        );
    }
}
