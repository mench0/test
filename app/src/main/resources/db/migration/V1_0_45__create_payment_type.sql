create table payment_type
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_by bigint references "user" (id),
    payment_name_en varchar(255)             not null,
    payment_name_ru varchar(255)             not null,
    payment_name_uz varchar(255)             not null,
    payment_value   varchar(3)               not null unique
);

create sequence payment_type_seq;