create table request_last_4_digits_PAN_mapping
(
    contact_limit_id      bigint       not null references contact_limit (id) on delete cascade,
    operation_type varchar(100) not null,
    primary key (contact_limit_id, operation_type)
);