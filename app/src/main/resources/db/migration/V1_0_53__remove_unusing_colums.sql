alter table terminal
    drop column model;

alter table modification_payment_system
    drop column contact_limit_currency,
    drop column contact_limit_floor_limit,
    drop column threshold,
    drop column target_percent,
    drop column max_target_percent,
    drop column pin_bypass_allowed,
    drop column pin_limit,
    drop column contactless_limit_currency,
    drop column contactless_limit_floor_limit,
    drop column no_cvm_limit,
    drop column on_device_cvm_limit,
    drop column no_device_cvm_limit;
