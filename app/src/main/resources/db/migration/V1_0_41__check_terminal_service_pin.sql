UPDATE terminal
SET service_pin = '12345'
WHERE service_pin ~ '[^\d]+';