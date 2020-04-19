--
-- Tables
--

CREATE TABLE IF NOT EXISTS ACTIVITIES (
   NAME           VARCHAR(50)   NOT NULL,
   URI            VARCHAR(100)  NOT NULL,
   PARAMS         TEXT,
   INS_DATE       TIMESTAMP     NOT NULL,
   UPD_DATE       TIMESTAMP,
   CONSTRAINT PK_ACTIVITIES PRIMARY KEY (NAME));
   
CREATE TABLE IF NOT EXISTS JOBS (
   ID             int           NOT NULL AUTO_INCREMENT,
   ACTIVITY       VARCHAR(50)   NOT NULL,
   PARAMS         TEXT,
   EXPRESSION     VARCHAR(50)   NOT NULL,
   LAST_EXECUTION TIMESTAMP,
   LAST_RESULT    TEXT,
   LAST_ERROR     VARCHAR(255),
   ELAPSED        int           DEFAULT 0 NOT NULL,
   INS_DATE       TIMESTAMP     NOT NULL,
   UPD_DATE       TIMESTAMP,
   CONSTRAINT PK_JOBS PRIMARY KEY (ID),
   CONSTRAINT FK_JOBS_ACT FOREIGN KEY (ACTIVITY) REFERENCES ACTIVITIES(NAME));
   
CREATE TABLE IF NOT EXISTS LOG_DATA (
   TABLENAME      VARCHAR(30)   NOT NULL,
   DB_USER        VARCHAR(30)   NOT NULL,
   PK_VALUE       VARCHAR(50)   NOT NULL,
   INS_DATE       TIMESTAMP     NOT NULL);

--
-- Indexes
--

CREATE INDEX IDX_ACT_URI ON ACTIVITIES(URI);

CREATE INDEX IDX_JOB_ACT ON JOBS(ACTIVITY);
CREATE INDEX IDX_JOB_LEX ON JOBS(LAST_EXECUTION);

-- mysql --user=root --password[=password] wcron
-- source C:/prj/dew/wcron/mysql/03_schema.sql;
--
-- Check
--
-- show tables;
--
-- desc activities;
-- desc jobs;
-- desc log_data;
--
-- show indexes from activities;
-- show indexes from jobs;
