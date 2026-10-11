create table terminal_acquirer_params_operations_mapping
(
    terminal_acquirer_params_id bigint       not null references terminal_acquirer_params (id) on delete cascade,
    operation                   varchar(100) not null,
    primary key (terminal_acquirer_params_id, operation)
);