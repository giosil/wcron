--
-- Views
--

CREATE OR REPLACE VIEW v_jobs AS
  SELECT A.NAME, A.URI, J.EXPRESSION, J.INS_DATE, J.UPD_DATE
  FROM ACTIVITIES A, JOBS J
  WHERE A.NAME = J.ACTIVITY;

-- mysql --user=root --password[=password] wcron
-- source C:/prj/dew/wcron/mysql/07_views.sql;
--
-- Check
--
-- show full tables in wcron where table_type like 'VIEW';
-- 
-- select table_schema,table_name from information_schema.tables where table_type like 'VIEW' and table_schema = 'WCRON';