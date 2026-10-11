ALTER TABLE merchant
    DROP CONSTRAINT IF EXISTS merchant_phone_key,
    DROP CONSTRAINT IF EXISTS merchant_email_key,
    DROP CONSTRAINT IF EXISTS merchant_inn_or_pinfl_key;
