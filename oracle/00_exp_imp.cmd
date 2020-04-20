rem sc start OracleOraDb11g_home1TNSListener
rem sc start OracleServiceORCL
rem Export 

exp WCRON/PASS123 FIlE=WCRON.dmp OWNER=WCRON STATISTICS=NONE

rem Import

imp system/password@ORCL file=WCRON.dmp log=imp_WCRON.log fromuser=WCRON touser=WCRON