create table migration_execution (
    id bigint primary key,
    name varchar(255) not null unique,
    version varchar(20) not null unique,
    execution_time timestamp with time zone not null
);

create sequence migration_execution_seq;
