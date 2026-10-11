alter table if exists modification_terminal
    drop column country_code_ignored,
    drop column service_pin_ignored,
    alter column country_code type varchar(3);

alter table if exists modification_acquirer
    drop column cashback_limit_ignored,
    drop column mac_master_key_index_ignored,
    drop column mac_working_key_index_ignored,
    drop column key_scheme_ignored;




