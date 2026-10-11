alter table acquirer
    add column rkl_host varchar(50),
    add column rkl_port integer,
    add column rkl_path varchar,
    add column rkl_use_ssl boolean,
    add column rkl_enable boolean
