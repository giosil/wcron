--
-- Data
--

INSERT INTO ACTIVITIES(NAME,URI,PARAMS,INS_DATE) VALUES('demo','mock','{"greeting":"hello"}',CURRENT_TIMESTAMP());

INSERT INTO JOBS(ACTIVITY,EXPRESSION,INS_DATE) VALUES('demo','1000 5000',CURRENT_TIMESTAMP());

COMMIT;

-- mysql --user=root --password[=password] wcron
-- source C:/prj/dew/wcron/mysql/08_data.sql;
--
-- Check
--
-- select * from activities;
-- select * from jobs;
-- select * from log_data;