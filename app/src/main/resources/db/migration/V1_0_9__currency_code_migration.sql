alter table contact_limit rename column currency_code to currency;
update contact_limit set currency = 'USD' where currency not in ('RUB', 'UZS');

alter table contactless_limit rename column currency_code to currency;
update contactless_limit set currency = 'USD' where currency not in ('RUB', 'UZS');
