rem Export
rem mysqldump -u user -p[password] [database] > file
rem mysqldump --user=user --password[=password] [database] > file

mysqldump --routines --user=root --password wcron > C:\prj\dew\wcron\mysql\wcron_dump.sql

rem Import (after create database: 01_setup.sql)
rem mysql --user=user --password[=password] [database] < file

mysql -u root -p wcron < C:\prj\dew\wcron\mysql\wcron_dump.sql


rem Connect to mysql:
rem mysql --user=root --password[=password] [database]
rem Some commands:
rem show databases;
rem select database() from dual;
rem use wcron
rem show tables;
rem show full tables in wcron where table_type like 'VIEW';
rem show triggers;
rem show function status where db='wcron';
rem show procedure status where db='wcron';
rem source C:/prj/dew/wcron/mysql/08_data.sql;