-- DROP USER WCRON CASCADE; 

-- DROP TABLESPACE WCRON_DATA INCLUDING CONTENTS AND DATAFILES;
-- DROP TABLESPACE WCRON_TEMP INCLUDING CONTENTS AND DATAFILES;

CREATE TABLESPACE WCRON_DATA DATAFILE '/u02/app/oracle/oradata/ORCL/WCRON_DATA.dbf' SIZE 20M REUSE AUTOEXTEND ON NEXT 512K MAXSIZE 8192M;
CREATE TEMPORARY TABLESPACE WCRON_TEMP TEMPFILE '/u02/app/oracle/oradata/ORCL/WCRON_TEMP.dbf' SIZE 5M AUTOEXTEND ON;

-- SELECT * FROM V$DATAFILE
-- alter system set DEFERRED_SEGMENT_CREATION=FALSE scope=both; // To include empty tables in export

-- To create user WCRON without prefix C## in Oracle 12:
ALTER SESSION SET "_ORACLE_SCRIPT"=true;

-- USER SQL
CREATE USER WCRON IDENTIFIED BY PASS123 DEFAULT TABLESPACE "WCRON_DATA" TEMPORARY TABLESPACE "WCRON_TEMP";
-- ROLE
GRANT "RESOURCE" TO WCRON WITH ADMIN OPTION;
GRANT "CONNECT" TO WCRON WITH ADMIN OPTION;
ALTER USER WCRON DEFAULT ROLE "RESOURCE","CONNECT";
-- SYSTEM PRIVILEGES
GRANT CREATE SESSION TO WCRON WITH ADMIN OPTION;
GRANT CREATE TABLE TO WCRON WITH ADMIN OPTION;
GRANT CREATE PROCEDURE TO WCRON WITH ADMIN OPTION;
GRANT CREATE SEQUENCE TO WCRON WITH ADMIN OPTION;
GRANT CREATE ANY TRIGGER TO WCRON WITH ADMIN OPTION;
GRANT DROP ANY TRIGGER TO WCRON WITH ADMIN OPTION;
GRANT CREATE ANY INDEX TO WCRON WITH ADMIN OPTION;
GRANT DROP ANY INDEX TO WCRON WITH ADMIN OPTION;
GRANT CREATE ANY VIEW TO WCRON WITH ADMIN OPTION;
GRANT DROP ANY VIEW TO WCRON WITH ADMIN OPTION;

-- To prevent ora-01950 in Oracle 12:
GRANT UNLIMITED TABLESPACE TO WCRON;

-- PURGE RECYCLEBIN;
