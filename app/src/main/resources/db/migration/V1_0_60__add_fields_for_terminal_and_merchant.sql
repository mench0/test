alter table terminal
    add column status               integer default 1,
    add column software_version     varchar;

UPDATE terminal
SET status = 0
WHERE archived = true;

alter table terminal_aud
    add column status           integer,
    add column software_version varchar;

alter table merchant
    add column phone                varchar unique,
    add column email                varchar unique,
    add column inn_or_pinfl         varchar unique;

alter table merchant_aud
    add column phone        varchar,
    add column email        varchar,
    add column inn_or_pinfl varchar;

CREATE TABLE skapps_outbox
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_by bigint references "user" (id),
    entity_type     varchar                  not null,
    is_bad_request  boolean                           default false,
    entity_id       bigint                   not null,
    payload         jsonb                    not null,
    retry_count     integer                  not null default 0
);

create sequence skapps_outbox_seq;
