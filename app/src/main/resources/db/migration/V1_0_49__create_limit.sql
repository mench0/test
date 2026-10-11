alter table contact_limit
    add column name varchar(50);
alter table contactless_limit
    add column name varchar(50);

-- create limit
create table "general_limit"
(
    id                   bigint primary key,
    created_at           timestamp with time zone not null,
    last_updated_at      timestamp with time zone not null,
    created_by           bigint references "user" (id),
    last_updated_by      bigint references "user" (id),
    name                 varchar(50)              not null,
    contact_limit_id     bigint references contact_limit (id),
    contactless_limit_id bigint references contactless_limit (id)
);

create sequence general_limit_seq;

create table payment_system_limit_mapping
(
    payment_system_id bigint not null references payment_system (id) on delete cascade,
    limit_id          bigint not null references "general_limit" (id) on delete cascade,
    primary key (payment_system_id, limit_id)
);

create table card_range_limit_mapping
(
    card_range_id bigint not null references card_range (id) on delete cascade,
    limit_id      bigint not null references general_limit (id) on delete cascade,
    primary key (card_range_id, limit_id)
);

-- create limits for payment_system
update contact_limit cl
set name = ps.name
from payment_system ps
where cl.id = ps.contact_limit_id;

update contactless_limit cll
set name = ps.name
from payment_system ps
where cll.id = ps.contactless_limit_id;

insert into general_limit
(id, created_at, last_updated_at, created_by, last_updated_by, name, contact_limit_id, contactless_limit_id)
select nextval('general_limit_seq'),
       now(),
       now(),
       2,
       2,
       concat(cl.name, '_', cll.name),
       cl.id,
       cll.id
from payment_system ps
         join contact_limit cl on ps.contact_limit_id = cl.id
         join contactless_limit cll on ps.contactless_limit_id = cll.id;

insert into "payment_system_limit_mapping"
    (payment_system_id, limit_id)
select ps.id,
       l.id
from payment_system ps
         join contact_limit cl on ps.contact_limit_id = cl.id
         join contactless_limit cll on ps.contactless_limit_id = cll.id
         join general_limit l on cl.id = l.contact_limit_id and cll.id = l.contactless_limit_id
where cl.id = ps.contact_limit_id
  and cll.id = ps.contactless_limit_id;

alter table payment_system
    drop column contactless_limit_id,
    drop column contact_limit_id;

-- limit from card_range
update contact_limit cl
set name = cr.name
from card_range cr
where cl.id = cr.contact_limit_id;

update contactless_limit cll
set name = cr.name
from card_range cr
where cll.id = cr.contactless_limit_id;

insert into general_limit
(id, created_at, last_updated_at, created_by, last_updated_by, name, contact_limit_id, contactless_limit_id)
select nextval('general_limit_seq'),
       now(),
       now(),
       2,
       2,
       concat(cl.name, '_', cll.name),
       cl.id,
       cll.id
from card_range cr
         join contact_limit cl on cr.contact_limit_id = cl.id
         join contactless_limit cll on cr.contactless_limit_id = cll.id;

insert into "card_range_limit_mapping"
    (card_range_id, limit_id)
select cr.id,
       l.id
from card_range cr
         join contact_limit cl on cr.contact_limit_id = cl.id
         join contactless_limit cll on cr.contactless_limit_id = cll.id
         join general_limit l on cl.id = l.contact_limit_id and cll.id = l.contactless_limit_id;

alter table card_range
    drop column contactless_limit_id,
    drop column contact_limit_id;

ALTER TABLE emv_app
    DROP CONSTRAINT emv_app_payment_system_id_fkey;

ALTER TABLE emv_app
    ADD CONSTRAINT emv_app_payment_system_id_fkey FOREIGN KEY (payment_system_id) REFERENCES payment_system (id) ON DELETE CASCADE;

ALTER TABLE contactless_app
    DROP CONSTRAINT contactless_app_payment_system_id_fkey;

ALTER TABLE contactless_app
    ADD CONSTRAINT contactless_app_payment_system_id_fkey FOREIGN KEY (payment_system_id) REFERENCES payment_system (id) ON DELETE CASCADE;

create table modification_limits_mapping
(
    modification_payment_system_Id bigint not null references modification_payment_system (id) on delete cascade,
    limit_id                       bigint not null,
    unique (modification_payment_system_Id, limit_id)
);