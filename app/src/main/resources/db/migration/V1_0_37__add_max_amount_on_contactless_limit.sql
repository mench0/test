alter table if exists contactless_limit
    add column max_amount bigint;

update contactless_limit
set max_amount = 999999999999
    where max_amount is null;

alter table if exists contactless_limit
    alter column max_amount set not null;