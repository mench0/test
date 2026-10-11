create table humo_setting
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_by bigint references "user" (id),
    humo_id         varchar(5)               not null,
    service_pin     varchar(10)              not null,
    terminal_type   varchar(100)             not null,
    acquirer_id     bigint                   not null references acquirer (id) on delete cascade,
    model_id        bigint                   not null references vendor_model (id) on delete cascade,
    unique (humo_id, model_id)
);

create sequence humo_setting_seq;
