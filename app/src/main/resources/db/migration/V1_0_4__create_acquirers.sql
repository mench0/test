create table acquirer (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    created_by bigint references "user"(id),
    last_updated_by bigint references "user"(id),
    name varchar(255) not null unique,
    using_mac_to_sign_requests boolean not null,
    iso_protocol varchar(100) not null,
    pin_master_key_index integer not null,
    pin_work_key_index integer not null,
    mac_master_key_index integer,
    mac_working_key_index integer,
    host varchar(50) not null,
    port integer not null,
    use_ssl boolean not null
);

create sequence acquirer_seq;

create table acquirer_available_operations_mapping (
    acquirer_id bigint not null references acquirer(id) on delete cascade,
    operation varchar(100) not null,
    primary key (acquirer_id, operation)
);

create table acquirer_available_currencies_mapping (
    acquirer_id bigint not null references acquirer(id) on delete cascade,
    currency varchar(100) not null,
    primary key (acquirer_id, currency)
);

create table acquirer_available_payment_systems_mapping (
    acquirer_id bigint not null references acquirer(id) on delete cascade,
    payment_system_id bigint not null references payment_system(id) on delete cascade,
    primary key (acquirer_id, payment_system_id)
);


