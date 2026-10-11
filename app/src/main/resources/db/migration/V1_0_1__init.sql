create table contact_limit (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    currency_code varchar(3) not null,
    threshold bigint not null,
    target_percent int not null,
    max_target_percent int not null,
    floor_limit bigint not null,
    pin_bypass_allowed boolean not null,
    pin_limit int not null
);

create sequence contact_limit_seq;

create table contactless_limit (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    currency_code varchar(3) not null,
    floor_limit bigint not null,
    no_cvm_limit bigint not null,
    on_device_cvm_limit bigint not null,
    no_device_cvm_limit bigint not null
);

create sequence contactless_limit_seq;

create table payment_system (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    name varchar(36) not null unique,
    avn varchar(4) not null,
    contactless_avn varchar(4) not null,
    contact_limit_id bigint references contact_limit(id) on delete set null,
    contactless_limit_id bigint references contactless_limit(id) on delete set null
);

create sequence payment_system_seq;

create table certificate (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    rid varchar(32) not null,
    index integer not null,
    exponent varchar(2) not null,
    modulus text not null,
    hash varchar(128) not null,
    valid_until timestamp with time zone not null,
    payment_system_id bigint not null references payment_system(id)
);

create sequence certificate_seq;

create table emv_app (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    aid varchar(32) not null,
    terminal_caps varchar(6) not null,
    terminal_additional_caps varchar(10) not null,
    tac_default varchar(10) not null,
    tac_online varchar(10) not null,
    tac_denial varchar(10) not null,
    tdol varchar(256) not null,
    ddot varchar(256) not null,
    payment_system_id bigint not null references payment_system(id)
);

create sequence emvapp_seq;

create table drl (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    contactless_limit_id bigint references contactless_limit(id),
    index int not null,
    operation_limit bigint not null,
    floor_limit bigint not null,
    no_cvm_limit bigint not null
);

create sequence drl_seq;

create table card_range (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    low varchar(19) not null,
    high varchar(19) not null,
    contact_limit_id bigint references contact_limit(id) on delete set null,
    contactless_limit_id bigint references contactless_limit(id) on delete set null,
    payment_system_id bigint not null references payment_system(id)
);

create sequence card_range_seq;

create table contactless_app (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    aid varchar(32) not null,
    kernel_id varchar(2) not null,
    kernel_configuration varchar(2) not null,
    terminal_caps varchar(6) not null,
    terminal_caps_cvm_not_required varchar(6) not null,
    terminal_additional_caps varchar(10) not null,
    tac_default varchar(10) not null,
    tac_online varchar(10) not null,
    tac_denial varchar(10) not null,
    tags jsonb not null,
    payment_system_id bigint not null references payment_system(id)
);

create sequence contactless_app_seq;
