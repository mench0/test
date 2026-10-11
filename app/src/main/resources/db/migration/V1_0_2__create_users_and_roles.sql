create table role (
    id bigint primary key,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    name varchar(255) not null unique,
    has_all_permissions boolean not null default false,
    system_user_role boolean not null default false
);

create sequence role_seq;

create table permissions_mapping (
    role_id bigint not null references role(id) on delete cascade,
    permission varchar(50) not null
);

create table "user" (
    id bigint primary key,
    user_type varchar(15) not null,
    created_at timestamp with time zone not null,
    last_updated_at timestamp with time zone not null,
    archived boolean not null default false,
    last_archived_at timestamp with time zone,
    last_restored_at timestamp with time zone,
    username varchar(255) not null unique,
    full_name varchar(255) not null,
    password_hash varchar(100) not null,
    password_last_set_at timestamp with time zone not null,
    role_id bigint not null references role(id)
);

create sequence user_seq;

create table system_user (
    id bigint primary key references "user"(id)
);

create table usual_user (
    id bigint primary key references "user"(id),
    email varchar(50) not null unique,
    locale varchar(10)
);

create table token (
    id bigint primary key,
    user_id bigint not null references "user"(id) on delete cascade,
    token_hash varchar(100) not null,
    token_type varchar(50) not null,
    created_at timestamp with time zone not null,
    life_time_in_minutes integer not null
);

create sequence token_seq;

alter table contact_limit add column created_by bigint references "user"(id);
alter table contact_limit add column last_updated_by bigint references "user"(id);

alter table contactless_limit add column created_by bigint references "user"(id);
alter table contactless_limit add column last_updated_by bigint references "user"(id);

alter table payment_system add column created_by bigint references "user"(id);
alter table payment_system add column last_updated_by bigint references "user"(id);

alter table certificate add column created_by bigint references "user"(id);
alter table certificate add column last_updated_by bigint references "user"(id);

alter table emv_app add column created_by bigint references "user"(id);
alter table emv_app add column last_updated_by bigint references "user"(id);

alter table drl add column created_by bigint references "user"(id);
alter table drl add column last_updated_by bigint references "user"(id);

alter table card_range add column created_by bigint references "user"(id);
alter table card_range add column last_updated_by bigint references "user"(id);

alter table contactless_app add column created_by bigint references "user"(id);
alter table contactless_app add column last_updated_by bigint references "user"(id);

alter table role add column created_by bigint references "user"(id);
alter table role add column last_updated_by bigint references "user"(id);

alter table "user" add column created_by bigint references "user"(id);
alter table "user" add column last_updated_by bigint references "user"(id);
