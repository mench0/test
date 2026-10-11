ALTER TABLE card_range
    DROP CONSTRAINT card_range_acquirer_id_fkey;

ALTER TABLE card_range
    ADD CONSTRAINT card_range_acquirer_id_fkey FOREIGN KEY (acquirer_id) REFERENCES acquirer (id) ON DELETE CASCADE;