alter table role add column terminal_default_role boolean not null default false;
create unique index role_terminal_default_role_key on role(terminal_default_role) where terminal_default_role = true;

alter table system_user add column terminal_serial_number varchar(100);
create unique index system_user_terminal_serial_number_key on system_user(terminal_serial_number) where terminal_serial_number is not null;
