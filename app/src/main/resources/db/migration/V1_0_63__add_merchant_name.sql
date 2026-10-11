alter table if exists terminal_acquirer_params
    add column merchant_name varchar;

alter table if exists terminal_acquirer_params_aud
    add column merchant_name varchar;

UPDATE terminal_acquirer_params
SET merchant_name = CONCAT(iso_merchant_id, '/', iso_terminal_id);

ALTER TABLE terminal_acquirer_params
    DROP CONSTRAINT terminal_acquirer_params_acquirer_id_terminal_id_key;

ALTER TABLE terminal_acquirer_params
    ADD CONSTRAINT terminal_acquirer_params_acquirer_id_terminal_id_iso_terminal_id_iso_merchant_id_key
        UNIQUE (acquirer_id, terminal_id, iso_terminal_id, iso_merchant_id);

