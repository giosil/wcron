# Import / Export procedures

## First check Oracle service

On Windows:

- `sc query OracleOraDb11g_home1TNSListener`
- `sc query OracleServiceORCL`
- `sc start OracleOraDb11g_home1TNSListener`
- `sc start OracleServiceORCL`

## Export

`exp WCRON/PASS123 FIlE=WCRON.dmp OWNER=WCRON STATISTICS=NONE`

## Import

`imp system/password@ORCL file=WCRON.dmp log=imp_WCRON.log fromuser=WCRON touser=WCRON`

## Connect to Oracle Instance

`sqlplus / AS SYSDBA`

### Some commands:

- `select * from all_users;`
- `connect WCRON;`
- `use wcron`
- `select table_name from all_tables;`
- `desc ACTIVITIES;`
