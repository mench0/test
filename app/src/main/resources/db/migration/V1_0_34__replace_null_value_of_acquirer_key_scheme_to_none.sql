update acquirer
set key_scheme = 'NONE'
where key_scheme is null;

alter table acquirer
    alter column key_scheme set not null;

