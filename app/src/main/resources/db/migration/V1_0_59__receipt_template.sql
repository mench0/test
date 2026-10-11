create table receipt_template
(
    id                bigserial primary key,
    created_at        timestamp with time zone not null,
    last_updated_at   timestamp with time zone not null,
    created_by        bigint references "user" (id),
    last_updated_by   bigint references "user" (id),
    merchant_id       bigint                   not null,
    external_id       bigint unique            not null,
    name              varchar(255)             not null,
    logo_file_id      varchar(255)             not null,
    active            boolean                  not null default true,
    valid_from        timestamp with time zone,
    valid_to          timestamp with time zone,
    -- header
    header_text       varchar(255)             not null,
    header_font_size  varchar(50)              not null,
    header_italic     boolean                  not null,
    header_bold       boolean                  not null,
    header_underline  boolean                  not null,
    header_text_align varchar(50)              not null,
    -- footer
    footer_text       varchar(255)             not null,
    footer_font_size  varchar(50)              not null,
    footer_italic     boolean                  not null,
    footer_bold       boolean                  not null,
    footer_underline  boolean                  not null,
    footer_text_align varchar(50)              not null
);

create sequence receipt_template_seq;
