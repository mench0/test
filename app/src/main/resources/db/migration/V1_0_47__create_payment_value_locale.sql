ALTER TABLE payment_type
    DROP COLUMN payment_name_en,
    DROP COLUMN payment_name_ru,
    DROP COLUMN payment_name_uz;

create table payment_type_locale
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_by bigint references "user" (id),
    payment_name    varchar(255)             not null,
    locale          varchar(2)               not null,
    payment_type_id bigint                   not null references payment_type (id) on delete cascade,
    unique (locale, payment_type_id)
);

create sequence payment_type_locale_seq;