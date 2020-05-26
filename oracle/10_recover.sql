-- sqlplus / as sysdba

shutdown abort;

exit

-- cp $HOME/oradata/orcl/control01.ctl $HOME/oradata/orcl/control01.ctl_backup

-- sqlplus / as sysdba

startup mount

select status, database_status from v$instance;

recover database using backup controlfile until cancel;

-- Open database

alter database open resetlogs;
