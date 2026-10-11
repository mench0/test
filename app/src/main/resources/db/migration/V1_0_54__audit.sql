create table custom_revision_entity
(
    rev       serial primary key,
    timestamp bigint not null,
    username  varchar(255),
    user_ip   varchar(50)
);

-- user + role
create table user_AUD
(
    id                   bigint   not null,
    rev                  integer  not null,
    revtype              smallint not null,
    user_type            varchar(15),
    archived             boolean,
    username             varchar(255),
    full_name            varchar(255),
    password_hash        varchar(100),
    password_last_set_at timestamp with time zone,
    role_id              bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table system_user_AUD
(
    id                     bigint  not null,
    rev                    integer not null,
    revtype                smallint,
    terminal_serial_number varchar(255),
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table usual_user_AUD
(
    id      bigint  not null,
    rev     integer not null,
    revtype smallint,
    email   varchar(255),
    locale  varchar(255),
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table role_AUD
(
    id                    bigint   not null,
    rev                   integer  not null,
    revtype               smallint not null,
    name                  varchar(255),
    has_all_permissions   boolean,
    system_user_role      boolean,
    terminal_default_role boolean,
    archived              boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table permissions_mapping_AUD
(
    rev        integer      not null,
    revtype    smallint     not null,
    role_id    bigint       not null,
    permission varchar(255) not null,
    primary key (role_id, permission, rev),
    foreign key (rev) references custom_revision_entity (rev)
);


-- vendor
create table vendor_AUD
(
    id      bigint   not null,
    rev     integer  not null,
    revtype smallint not null,
    name    varchar(100),
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table vendor_model_AUD
(
    id        bigint   not null,
    rev       integer  not null,
    revtype   smallint not null,
    name      varchar(100),
    vendor_id bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- payment system type
create table payment_type_AUD
(
    id            bigint   not null,
    rev           integer  not null,
    revtype       smallint not null,
    payment_value varchar(255),
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table payment_type_locale_AUD
(
    id              bigint   not null,
    rev             integer  not null,
    revtype         smallint not null,
    payment_name    varchar(255),
    locale          varchar(10),
    payment_type_id bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- humo settings
create table humo_setting_AUD
(
    id            bigint   not null,
    rev           integer  not null,
    revtype       smallint not null,
    humo_id       varchar(5),
    service_pin   varchar(10),
    terminal_type varchar(100),
    model_id      bigint,
    acquirer_id   bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- merchant
create table merchant_AUD
(
    id       bigint   not null,
    rev      integer  not null,
    revtype  smallint not null,
    name     varchar(255),
    address  varchar(255),
    archived boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- license
create table license_AUD
(
    id            bigint   not null,
    rev           integer  not null,
    revtype       smallint not null,
    serial_number varchar(100),
    license_code  varchar(100),
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- certificate
create table certificate_AUD
(
    id                bigint   not null,
    rev               integer  not null,
    revtype           smallint not null,
    rid               varchar(32),
    index             integer,
    exponent          varchar(6),
    modulus           text,
    hash              varchar(128),
    valid_until       timestamp with time zone,
    payment_system_id bigint,
    archived          boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- EMV app
create table emv_app_AUD
(
    id                       bigint   not null,
    rev                      integer  not null,
    revtype                  smallint not null,
    aid                      varchar(32),
    terminal_caps            varchar(6),
    terminal_additional_caps varchar(10),
    tac_default              varchar(10),
    tac_online               varchar(10),
    tac_denial               varchar(10),
    tdol                     varchar(256),
    ddol                     varchar(256),
    payment_system_id        bigint,
    terminal_type            varchar(100),
    archived                 boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- contactless app
create table contactless_app_AUD
(
    id                             bigint   not null,
    rev                            integer  not null,
    revtype                        smallint not null,
    aid                            varchar(32),
    kernel_id                      varchar(2),
    kernel_configuration           varchar(3),
    terminal_caps                  varchar(6),
    terminal_caps_cvm_not_required varchar(6),
    terminal_additional_caps       varchar(10),
    tac_default                    varchar(10),
    tac_online                     varchar(10),
    tac_denial                     varchar(10),
    payment_system_id              bigint,
    tags                           jsonb,
    terminal_type                  varchar(100),
    archived                       boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- contactless limit
create table contact_limit_AUD
(
    id                 bigint   not null,
    rev                integer  not null,
    revtype            smallint not null,
    name               varchar(50),
    currency           varchar(3),
    threshold          bigint,
    target_percent     integer,
    max_target_percent integer,
    floor_limit        bigint,
    pin_bypass_allowed boolean,
    pin_limit          integer,
    archived           boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table request_last_4_digits_pan_mapping_AUD
(
    contact_limit_id bigint       not null,
    rev              integer      not null,
    revtype          smallint     not null,
    operation_type   varchar(100) not null,
    primary key (contact_limit_id, rev, operation_type),
    foreign key (rev) references custom_revision_entity (rev)
);

-- DRL
create table drl_AUD
(
    id                   bigint   not null,
    rev                  integer  not null,
    revtype              smallint not null,
    contactless_limit_id bigint,
    index                integer,
    operation_limit      bigint,
    floor_limit          bigint,
    no_cvm_limit         bigint,
    archived             boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- contactless limit
create table contactless_limit_AUD
(
    id                  bigint   not null,
    rev                 integer  not null,
    revtype             smallint not null,
    name                varchar(50),
    currency            varchar(3),
    floor_limit         bigint,
    no_cvm_limit        bigint,
    on_device_cvm_limit bigint,
    no_device_cvm_limit bigint,
    max_amount          bigint,
    archived            boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table contactless_limit_drl_list_AUD
(
    contactless_limit_id bigint   not null,
    rev                  integer  not null,
    revtype              smallint not null,
    drl_id               bigint   not null,
    primary key (contactless_limit_id, rev, drl_id),
    foreign key (rev) references custom_revision_entity (rev)
);

-- general limit
create table general_limit_AUD
(
    id                   bigint   not null,
    rev                  integer  not null,
    revtype              smallint not null,
    name                 varchar(50),
    contact_limit_id     bigint,
    contactless_limit_id bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- payment system
create table payment_system_AUD
(
    id              bigint   not null,
    rev             integer  not null,
    revtype         smallint not null,
    name            varchar(36),
    avn             varchar(4),
    contactless_avn varchar(4),
    archived        boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table payment_system_limit_mapping_AUD
(
    payment_system_id bigint   not null,
    rev               integer  not null,
    revtype           smallint not null,
    limit_id          bigint   not null,
    primary key (payment_system_id, rev, limit_id),
    foreign key (rev) references custom_revision_entity (rev)
);

-- card range
create table card_range_AUD
(
    id          bigint   not null,
    rev         integer  not null,
    revtype     smallint not null,
    low         varchar(19),
    high        varchar(19),
    name        varchar(100),
    acquirer_id bigint,
    archived    boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table card_range_limit_mapping_AUD
(
    card_range_id bigint   not null,
    limit_id      bigint   not null,
    rev           integer  not null,
    revtype       smallint not null,
    primary key (card_range_id, limit_id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- terminal group
create table terminal_group_AUD
(
    id          bigint   not null,
    rev         integer  not null,
    revtype     smallint not null,
    name        varchar(255),
    parent_path varchar,
    archived    boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- acquirer
create table acquirer_AUD
(
    id                         bigint   not null,
    rev                        integer  not null,
    revtype                    smallint not null,
    name                       varchar(255),
    using_mac_to_sign_requests boolean,
    iso_protocol               varchar(100),
    pin_master_key_index       integer,
    pin_work_key_index         integer,
    mac_master_key_index       integer,
    mac_working_key_index      integer,
    host                       varchar(50),
    port                       integer,
    use_ssl                    boolean,
    cashback_limit             bigint,
    key_scheme                 varchar(25),
    rkl_host                   varchar(50),
    rkl_port                   integer,
    rkl_path                   varchar,
    rkl_use_ssl                boolean,
    rkl_enable                 boolean,
    archived                   boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table acquirer_available_currencies_mapping_AUD
(
    rev         integer      not null,
    acquirer_id bigint       not null,
    currency    varchar(100) not null,
    revtype     smallint     not null,
    primary key (rev, acquirer_id, currency),
    foreign key (rev) references custom_revision_entity (rev)
);

create table acquirer_available_operations_mapping_AUD
(
    id              bigint   not null,
    rev             integer  not null,
    revtype         smallint not null,
    acquirer_id     bigint,
    operation       varchar(100),
    operation_order integer,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table acquirer_available_payment_systems_mapping_AUD
(
    rev               integer  not null,
    acquirer_id       bigint   not null,
    payment_system_id bigint   not null,
    revtype           smallint not null,
    primary key (rev, acquirer_id, payment_system_id),
    foreign key (rev) references custom_revision_entity (rev)
);

-- terminal
create table terminal_AUD
(
    id                    bigint   not null,
    rev                   integer  not null,
    revtype               smallint not null,
    serial_number         varchar(100),
    model_id              bigint,
    default_terminal_type varchar(100),
    merchant_id           bigint,
    service_pin           varchar(10),
    group_id              bigint,
    address               varchar(255),
    country               varchar(100),
    config_updated_date   timestamp with time zone,
    archived              boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table terminal_acquirer_params_AUD
(
    id                   bigint   not null,
    rev                  integer  not null,
    revtype              smallint not null,
    iso_terminal_id      varchar(8),
    iso_merchant_id      varchar(15),
    pin_master_key_kcv   varchar(6),
    acquirer_id          bigint,
    terminal_id          bigint,
    rki_enabled          boolean,
    mcc                  varchar(4),
    type                 varchar(100),
    kcv_updated_date     timestamp with time zone,
    params_payment_types jsonb,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table terminal_acquirer_params_operations_mapping_AUD
(
    id                          bigint   not null,
    rev                         integer  not null,
    revtype                     smallint not null,
    terminal_acquirer_params_id bigint,
    operation                   varchar(100),
    operation_order             integer,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

-- modification
create table modification_terminal_AUD
(
    id          bigint   not null,
    rev         integer  not null,
    revtype     smallint not null,
    country     varchar(255),
    service_pin varchar(255),
    group_id    bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table modification_acquirer_AUD
(
    id                    bigint   not null,
    rev                   integer  not null,
    revtype               smallint not null,
    cashback_limit        bigint,
    acquirer_id           bigint,
    group_id              bigint,
    pin_master_key_index  integer,
    pin_work_key_index    integer,
    mac_master_key_index  integer,
    mac_working_key_index integer,
    key_scheme            varchar(25),
    host                  varchar(50),
    port                  integer,
    use_ssl               boolean,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table modification_payment_system_AUD
(
    id                bigint   not null,
    rev               integer  not null,
    revtype           smallint not null,
    avn               varchar(4),
    contactless_avn   varchar(4),
    payment_system_id bigint,
    group_id          bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table modification_limits_mapping_AUD
(
    modification_payment_system_id bigint   not null,
    limit_id                       bigint   not null,
    rev                            integer  not null,
    revtype                        smallint not null,
    primary key (modification_payment_system_id, limit_id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table modification_contactless_app_AUD
(
    id                             bigint   not null,
    rev                            integer  not null,
    revtype                        smallint not null,
    aid                            varchar(255),
    kernel_id                      varchar(255),
    kernel_configuration           varchar(255),
    terminal_caps                  varchar(255),
    terminal_caps_cvm_not_required varchar(255),
    terminal_additional_caps       varchar(255),
    tac_default                    varchar(255),
    tac_online                     varchar(255),
    tac_denial                     varchar(255),
    contactless_app_id             bigint,
    tags                           jsonb,
    tags_ignored                   boolean,
    terminal_type                  varchar(100),
    terminal_type_ignored          boolean,
    modification_payment_system_id bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);

create table modification_emv_app_AUD
(
    id                             bigint   not null,
    rev                            integer  not null,
    revtype                        smallint not null,
    aid                            varchar(255),
    terminal_caps                  varchar(255),
    terminal_additional_caps       varchar(255),
    tac_default                    varchar(255),
    tac_online                     varchar(255),
    tac_denial                     varchar(255),
    tdol                           varchar(255),
    ddol                           varchar(255),
    emv_app_id                     bigint,
    terminal_type                  varchar(100),
    terminal_type_ignored          boolean,
    modification_payment_system_id bigint,
    primary key (id, rev),
    foreign key (rev) references custom_revision_entity (rev)
);
