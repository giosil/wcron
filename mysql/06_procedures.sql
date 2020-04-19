--
-- Procedures
--

DELIMITER $$
CREATE PROCEDURE clean_data()
BEGIN

  DELETE FROM LOG_DATA;
   
  DELETE FROM JOBS;
   
  DELETE FROM ACTIVITIES;
  
END$$
DELIMITER ;

-- mysql --user=root --password[=password] wcron
-- source C:/prj/dew/wcron/mysql/06_procedures.sql;
--
-- Check
--
-- show procedure status where db='wcron';
--
-- Usage:
--
-- call clean_data();
--
-- commit;
--