create table modification_terminal
(
    id                   bigint primary key,
    created_at           timestamp with time zone not null,
    last_updated_at      timestamp with time zone not null,
    created_by           bigint references "user" (id),
    last_updated_by      bigint references "user" (id),
    country_code         varchar,
    country_code_ignored boolean,
    service_pin          varchar(10),
    service_pin_ignored  boolean,
    group_id             bigint unique references terminal_group (id) on delete cascade
);

create sequence modification_terminal_seq;

create table modification_acquirer
(
    id                            bigint primary key,
    created_at                    timestamp with time zone not null,
    last_updated_at               timestamp with time zone not null,
    created_by                    bigint references "user" (id),
    last_updated_by               bigint references "user" (id),
    acquirer_id                   bigint                   not null references acquirer (id) on delete cascade,
    group_id                      bigint unique references terminal_group (id) on delete cascade,
    cashback_limit                bigint,
    cashback_limit_ignored        boolean,
-- KeySettings
    pin_master_key_index          integer,
    pin_work_key_index            integer,
    mac_master_key_index          integer,
    mac_master_key_index_ignored  boolean,
    mac_working_key_index         integer,
    mac_working_key_index_ignored boolean,
    key_scheme                    varchar(25),
    key_scheme_ignored            boolean,
-- PaymentHost
    host                          varchar(50),
    port                          integer,
    use_ssl                       boolean
);

create sequence modification_acquirer_seq;

create table modification_payment_system
(
    id                            bigint primary key,
    created_at                    timestamp with time zone not null,
    last_updated_at               timestamp with time zone not null,
    created_by                    bigint references "user" (id),
    last_updated_by               bigint references "user" (id),
    payment_system_id             bigint                   not null references payment_system (id) on delete cascade,
    group_id                      bigint unique references terminal_group (id) on delete cascade,
    avn                           varchar(4),
    contactless_avn               varchar(4),
-- ContactLimit
    contact_limit_id              bigint,
    contact_limit_currency        varchar(3),
    contact_limit_floor_limit     bigint,
    threshold                     bigint,
    target_percent                integer,
    max_target_percent            integer,
    pin_bypass_allowed            boolean,
    pin_limit                     integer,
-- ContactlessLimit
    contactless_limit_id          bigint,
    contactless_limit_currency    varchar(3),
    contactless_limit_floor_limit bigint,
    no_cvm_limit                  bigint,
    on_device_cvm_limit           bigint,
    no_device_cvm_limit           bigint
);

create sequence modification_payment_system_seq;

create table modification_emv_app
(
    id                             bigint primary key,
    created_at                     timestamp with time zone not null,
    last_updated_at                timestamp with time zone not null,
    created_by                     bigint references "user" (id),
    last_updated_by                bigint references "user" (id),
    emv_app_id                     bigint                   not null references emv_app (id) on delete cascade,
    modification_payment_system_id bigint                   not null references modification_payment_system (id) on delete cascade,
    aid                            varchar(32),
    terminal_caps                  varchar(6),
    terminal_additional_caps       varchar(10),
    tac_default                    varchar(10),
    tac_online                     varchar(10),
    tac_denial                     varchar(10),
    terminal_type                  varchar(100),
    terminal_type_ignored          boolean,
    tdol                           varchar(256),
    ddol                           varchar(256)
);

create sequence modification_emv_app_seq;

create table modification_contactless_app
(
    id                             bigint primary key,
    created_at                     timestamp with time zone not null,
    last_updated_at                timestamp with time zone not null,
    created_by                     bigint references "user" (id),
    last_updated_by                bigint references "user" (id),
    contactless_app_id             bigint                   not null references contactless_app (id) on delete cascade,
    modification_payment_system_id bigint                   not null references modification_payment_system (id) on delete cascade,
    aid                            varchar(32),
    terminal_caps                  varchar(6),
    terminal_additional_caps       varchar(10),
    tac_default                    varchar(10),
    tac_online                     varchar(10),
    tac_denial                     varchar(10),
    terminal_type                  varchar(100),
    terminal_type_ignored          boolean,
    kernel_id                      varchar(2),
    kernel_configuration           varchar(3),
    terminal_caps_cvm_not_required varchar(6),
    tags                           jsonb,
    tags_ignored                   boolean
);

create sequence modification_contactless_app_seq;