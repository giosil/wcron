# Import / Export procedures

## First check MySQL service

On Linux:

- `service mysql status`
- `service mysql start`

On Windows:

- `sc query MySQL56`
- `sc start MySQL56`

## Export

- `mysqldump -u user -p[password] [database] > file`
- `mysqldump --user=user --password[=password] [database] > file`

Example (add --routines to export functions and procedures):

`mysqldump --routines --user=root --password wcron > C:\prj\dew\wcron\mysql\wcron_dump.sql`

## Import (after create database: 01_setup.sql)

- `mysql --user=user --password[=password] [database] < file`

Example:

`mysql -u root -p wcron < C:\prj\dew\wcron\mysql\wcron_dump.sql`

## Connect to mysql

`mysql --user=root --password[=password] [database]`

### Some commands:

- `show databases;`
- `select database() from dual;`
- `select @@datadir;`
- `purge binary logs before '2024-07-01 00:00:00';
- `use wcron`
- `show tables;`
- `show full tables in wcron where table_type like 'VIEW';`
- `show triggers;`
- `show function status where db='wcron';`
- `show procedure status where db='wcron';`
- `source C:/prj/dew/wcron/mysql/08_data.sql;`