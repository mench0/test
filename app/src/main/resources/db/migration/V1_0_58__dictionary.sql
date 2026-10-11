delete
from card_type;
delete
from card_type_aud;

alter table card_type
    add column name varchar not null unique,
    drop column code,
    drop column card_audience;

alter table card_type_aud
    add column name varchar,
    drop column code,
    drop column card_audience;

create table card_code
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_by bigint references "user" (id),
    code            varchar(2)               not null unique,
    card_type_id    bigint                   not null references card_type (id) on delete cascade
);

create sequence card_code_seq;

create table card_code_AUD
(
    id           bigint   not null,
    rev          integer  not null,
    revtype      smallint not null,
    code         varchar(2),
    card_type_id bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table card_bin_range
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_by bigint references "user" (id),
    start_bin       varchar(19)              not null,
    end_bin         varchar(19)              not null,
    card_type_id    bigint                   not null references card_type (id) on delete cascade
);

create sequence card_bin_range_seq;

create table card_bin_range_AUD
(
    id           bigint   not null,
    rev          integer  not null,
    revtype      smallint not null,
    start_bin    varchar(19),
    end_bin      varchar(19),
    card_type_id bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);