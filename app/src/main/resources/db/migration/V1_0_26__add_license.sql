create table license
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_at timestamp with time zone not null,
    last_updated_by bigint references "user" (id),
    serial_number   varchar(100)             not null unique,
    license_code    varchar(100)             not null unique
);

create sequence license_seq;