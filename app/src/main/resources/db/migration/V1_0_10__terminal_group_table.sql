create table terminal_group (
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

create sequence terminal_group_seq;

drop table merchant_group cascade;
drop sequence merchant_group_seq;

alter table merchant drop column group_id;

alter table terminal add column group_id bigint references terminal_group(id);
