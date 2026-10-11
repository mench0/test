alter table terminal
    add column config_updated_date timestamp with time zone;

alter table terminal_acquirer_params
    add column kcv_updated_date timestamp with time zone;