create table country (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    created_by bigint references "user"(id),
    last_updated_by bigint references "user"(id),
    name varchar(100) not null unique,
    code integer not null unique
);

create sequence country_seq;

create table merchant_group (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    created_by bigint references "user"(id),
    last_updated_by bigint references "user"(id),
    name varchar(255) not null unique
);

create sequence merchant_group_seq;

create table merchant (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    created_by bigint references "user"(id),
    last_updated_by bigint references "user"(id),
    name varchar(255) not null unique,
    address varchar(255) not null,
    group_id bigint references merchant_group(id)
);

create sequence merchant_seq;

create table terminal (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    created_by bigint references "user"(id),
    last_updated_by bigint references "user"(id),
    serial_number varchar(100) not null,
    model varchar(100) not null,
    type varchar(100) not null,
    iso_terminal_id varchar(8) not null,
    iso_merchant_id varchar(15) not null,
    merchant_id bigint not null references merchant(id),
    service_pin varchar(10),
    country_id bigint not null references country(id)
);

create sequence terminal_seq;

create unique index terminal_serial_number_key on terminal(serial_number) where archived = false;

create table terminals_acquirers_mapping (
    acquirer_id bigint not null references acquirer(id) on delete cascade,
    terminal_id bigint not null references terminal(id) on delete cascade,
    primary key (acquirer_id, terminal_id)
);
