alter table terminal
    drop column iso_terminal_id,
    drop column iso_merchant_id;

create table terminal_acquirer_params (
    id                 bigint                   primary key,
    created_at         timestamp with time zone not null,
    created_by         bigint                   references "user"(id),
    last_updated_at    timestamp with time zone not null,
    last_updated_by    bigint                   references "user"(id),
    iso_terminal_id    varchar(8)               not null,
    iso_merchant_id    varchar(15)              not null,
    pin_master_key_KCV varchar(6),
    acquirer_id        bigint                   not null references acquirer(id) on delete cascade,
    terminal_id        bigint                   not null references terminal(id) on delete cascade,
    unique (acquirer_id, terminal_id)
);

create sequence terminal_acquirer_params_seq;
