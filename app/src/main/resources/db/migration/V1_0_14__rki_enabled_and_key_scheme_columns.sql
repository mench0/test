alter table acquirer add column key_scheme varchar(25);

alter table terminal_acquirer_params add column rki_enabled boolean not null default false;

drop table terminals_acquirers_mapping cascade;
