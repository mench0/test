alter table terminal add column country varchar(100);

UPDATE terminal t
SET country = c.name
FROM country c
WHERE t.country_id = c.id;

alter table terminal drop column country_id;
alter table terminal alter column country set not null;

drop table country;
drop sequence country_seq;

DELETE FROM permissions_mapping WHERE permission in ('COUNTRY_READ', 'COUNTRY_WRITE');

alter table modification_terminal rename column country_code to country;
alter table modification_terminal alter column country type varchar(100);
