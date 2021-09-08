explain plan for SELECT * FROM JOBS WHERE ACTIVITY='demo'; 

select plan_table_output from table(dbms_xplan.display());

