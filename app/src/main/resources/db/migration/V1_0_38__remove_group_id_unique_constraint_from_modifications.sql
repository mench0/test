alter table if exists modification_payment_system
    drop constraint modification_payment_system_group_id_key;

alter table if exists modification_payment_system
    add unique (payment_system_id, group_id);

alter table if exists modification_acquirer
    drop constraint modification_acquirer_group_id_key;

alter table if exists modification_acquirer
    add unique (acquirer_id, group_id);
