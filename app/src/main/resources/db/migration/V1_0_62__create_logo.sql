create table if not exists payment_screen_logo
(
    id              bigint primary key,
    created_at      timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    created_by      bigint references "user" (id),
    last_updated_by bigint references "user" (id),
    name            varchar                  not null unique,
    image_id        varchar                   not null

);

create sequence if not exists payment_screen_logo_seq;

create table if not exists terminal_group_payment_screen_logo
(
    id                        bigint primary key            not null,
    terminal_group_id         bigint                        not null
        references terminal_group (id) on delete cascade,
    payment_screen_logo_id    bigint                        not null
        references payment_screen_logo (id) on delete cascade,
    created_at                timestamp with time zone      not null,
    last_updated_at           timestamp with time zone      not null,
    created_by                bigint references "user" (id) not null,
    last_updated_by           bigint references "user" (id) not null,
    payment_screen_logo_order integer                       not null
);

create sequence if not exists terminal_group_payment_screen_logo_seq;

create table if not exists payment_screen_logo_AUD
(
    id       bigint   not null,
    rev      integer  not null,
    revtype  smallint not null,
    name     varchar,
    image_id varchar,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table if not exists terminal_group_payment_screen_logo_AUD
(
    id                        bigint   not null,
    terminal_group_id         bigint,
    payment_screen_logo_id    bigint,
    payment_screen_logo_order integer,
    rev                       integer  not null,
    revtype                   smallint not null,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);
