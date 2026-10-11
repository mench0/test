create table card_type
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_by bigint references "user" (id),
    code            varchar(2)               not null unique,
    card_audience   varchar                  not null
);

create sequence card_type_seq;

create table card_type_AUD
(
    id            bigint   not null,
    rev           integer  not null,
    revtype       smallint not null,
    code          varchar(2),
    card_audience varchar,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);