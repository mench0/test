alter table if exists terminal_acquirer_params
    add column params_order integer;

alter table if exists terminal_acquirer_params_aud
    add column params_order integer;

UPDATE terminal_acquirer_params t
SET params_order = sub.rn
FROM (SELECT id, ROW_NUMBER() OVER (PARTITION BY terminal_id ORDER BY id) AS rn
      FROM terminal_acquirer_params) sub
WHERE t.id = sub.id;


