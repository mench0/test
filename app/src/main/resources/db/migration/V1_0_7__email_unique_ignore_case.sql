alter table usual_user drop constraint usual_user_email_key;
create unique index usual_user_email_key on usual_user (lower(email));
