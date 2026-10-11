ALTER TABLE card_range
    ADD COLUMN acquirer_id BIGINT REFERENCES acquirer (id);