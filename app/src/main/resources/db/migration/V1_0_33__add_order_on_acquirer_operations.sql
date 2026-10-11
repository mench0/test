--add order on acquirer_available_operations_mapping
create table if not exists acquirer_available_operations_mapping_new
(
    id               bigint     primary key  not null,
    acquirer_id      bigint       not null
        references acquirer(id)
            on delete cascade,
    operation        varchar(100) not null,
    created_at       timestamp with time zone not null,
    last_updated_at  timestamp with time zone not null,
    created_by       bigint references "user" (id) not null,
    last_updated_by  bigint references "user" (id) not null,
    operation_order  integer not null
);
create sequence if not exists acquirer_operation_seq;

insert into acquirer_available_operations_mapping_new
(id, acquirer_id, operation, created_at, last_updated_at, created_by, last_updated_by, operation_order)
select
    nextval('acquirer_operation_seq'),
    acquirer_id,
    operation,
    now(),
    now(),
    2,
    2,
    row_number() over (partition by acquirer_id order by operation) as operation_order
from
    acquirer_available_operations_mapping;

drop table if exists acquirer_available_operations_mapping;

alter table acquirer_available_operations_mapping_new rename to acquirer_available_operations_mapping;


--add order on terminal_acquirer_params_operations_mapping
create table if not exists terminal_acquirer_params_operations_mapping_new
(
    id               bigint      primary key  not null,
    terminal_acquirer_params_id      bigint       not null
        references terminal_acquirer_params
            on delete cascade,
    operation        varchar(100) not null,
    created_at       timestamp with time zone not null,
    last_updated_at  timestamp with time zone not null,
    created_by       bigint references "user" (id) not null,
    last_updated_by  bigint references "user" (id) not null,
    operation_order  integer not null
);
create sequence if not exists terminal_acquirer_operation_seq;

insert into terminal_acquirer_params_operations_mapping_new
(id, terminal_acquirer_params_id, operation, created_at, last_updated_at, created_by, last_updated_by, operation_order)
select
    nextval('terminal_acquirer_operation_seq'),
    terminal_acquirer_params_id,
    operation,
    now(),
    now(),
    2,
    2,
    row_number() over (partition by terminal_acquirer_params_id order by operation) as operation_order
from
    terminal_acquirer_params_operations_mapping;

drop table if exists terminal_acquirer_params_operations_mapping;

alter table terminal_acquirer_params_operations_mapping_new rename to terminal_acquirer_params_operations_mapping;