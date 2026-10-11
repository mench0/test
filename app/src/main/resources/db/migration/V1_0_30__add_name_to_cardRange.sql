ALTER TABLE card_range
    ADD COLUMN name VARCHAR(100);

UPDATE card_range cr
SET name  = concat(cr.low, ' - ', cr.high)
WHERE name IS NULL;

ALTER TABLE card_range
    ALTER COLUMN name SET NOT NULL;