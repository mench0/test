ALTER TABLE terminal
    ADD COLUMN software_name   VARCHAR,
    ADD COLUMN mode            VARCHAR,
    ADD COLUMN software_vendor VARCHAR,
    ADD COLUMN software_model  VARCHAR;

ALTER TABLE terminal_aud
    ADD COLUMN software_name   VARCHAR,
    ADD COLUMN mode            VARCHAR,
    ADD COLUMN software_vendor VARCHAR,
    ADD COLUMN software_model  VARCHAR;
