--
-- Functions
--

DELIMITER $$
CREATE FUNCTION every_sec(nSeconds INT) RETURNS VARCHAR(20) DETERMINISTIC
BEGIN
  DECLARE nMillis INT;
  DECLARE vcResult VARCHAR(20);
  
  SET nMillis = nSeconds * 1000;
  
  SET vcResult = CONCAT(CAST(nMillis AS CHAR), ' ', CAST(nMillis AS CHAR));
  
  RETURN vcResult;
END$$
DELIMITER ;

-- mysql --user=root --password[=password] wcron
-- source C:/prj/dew/wcron/mysql/05_functions.sql;
--
-- Check
--
-- show function status where db='wcron';