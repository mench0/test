create table vendor
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_by bigint references "user" (id),
    name            varchar(100)             not null unique
);

create sequence vendor_seq;

create table vendor_model
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_by bigint references "user" (id),
    name            varchar(100)             not null,
    vendor_id       bigint                   not null references vendor (id) on delete cascade
);

create sequence vendor_model_seq;

ALTER TABLE terminal
    ADD COLUMN model_id BIGINT references vendor_model (id),
    ALTER COLUMN model DROP NOT NULL;
