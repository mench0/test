alter table receipt_template
    alter column valid_from set not null;

alter table receipt_template
    alter column valid_to set not null;
