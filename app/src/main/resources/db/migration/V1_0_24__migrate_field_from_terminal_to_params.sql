ALTER TABLE terminal_acquirer_params
    ADD COLUMN IF NOT EXISTS mcc VARCHAR(4);
ALTER TABLE terminal_acquirer_params
    ADD COLUMN IF NOT EXISTS type VARCHAR(100);

UPDATE terminal_acquirer_params tap
SET mcc  = t.mcc,
    type = t.type
FROM terminal t
WHERE tap.terminal_id = t.id;

ALTER TABLE terminal_acquirer_params
    ALTER COLUMN mcc SET NOT NULL;
ALTER TABLE terminal_acquirer_params
    ALTER COLUMN type SET NOT NULL;

ALTER TABLE terminal
    DROP COLUMN mcc,
    DROP COLUMN type;