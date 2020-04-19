--
-- Drop tables
--

DROP TABLE IF EXISTS `jobs`;

DROP TABLE IF EXISTS `activities`;

DROP TABLE IF EXISTS `log_data`;

-- Drop views

DROP VIEW IF EXISTS `v_jobs`;

-- Drop functions

DROP FUNCTION IF EXISTS `every_sec`;

-- Drop procedures

DROP PROCEDURE IF EXISTS `clean_data`;

-- mysql --user=root --password[=password] wcron
-- source C:/prj/dew/wcron/mysql/02_drop.sql;
--