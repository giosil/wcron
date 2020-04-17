--
-- Tables
--

CREATE TABLE ACTIVITIES (
   NAME           VARCHAR2(50)   NOT NULL,
   URI            VARCHAR2(100)  NOT NULL,
   PARAMS         VARCHAR2(4000),
   INS_DATE       TIMESTAMP      NOT NULL,
   UPD_DATE       TIMESTAMP,
   CONSTRAINT PK_ACTIVITIES PRIMARY KEY (NAME));
   
CREATE TABLE JOBS (
   ID             NUMBER(10)     NOT NULL,
   ACTIVITY       VARCHAR2(50)   NOT NULL,
   PARAMS         VARCHAR2(4000),
   EXPRESSION     VARCHAR(50)    NOT NULL,
   LAST_EXECUTION TIMESTAMP,
   LAST_RESULT    VARCHAR(4000),
   LAST_ERROR     VARCHAR(255),
   ELAPSED        NUMBER(10)     DEFAULT 0 NOT NULL,
   INS_DATE       TIMESTAMP      NOT NULL,
   UPD_DATE       TIMESTAMP,
   CONSTRAINT PK_JOBS PRIMARY KEY (ID),
   CONSTRAINT FK_JOBS_ACT FOREIGN KEY (ACTIVITY) REFERENCES ACTIVITIES(NAME));
   
CREATE TABLE LOG_DATA (
   TABLENAME      VARCHAR2(30)   NOT NULL,
   DB_USER        VARCHAR2(30)   NOT NULL,
   PK_VALUE       VARCHAR2(50)   NOT NULL,
   INS_DATE       TIMESTAMP      NOT NULL);

--
-- Indexes
--

CREATE INDEX IDX_ACT_URI ON ACTIVITIES(URI);

CREATE INDEX IDX_JOB_ACT ON JOBS(ACTIVITY);
CREATE INDEX IDX_JOB_LEX ON JOBS(LAST_EXECUTION);

--
-- Sequences
--

CREATE SEQUENCE SEQ_JOBS START WITH 1 MAXVALUE 999999999999 MINVALUE 1 NOCYCLE NOCACHE NOORDER;

--
-- Comments
-- 

COMMENT ON TABLE ACTIVITIES IS 'Activities table';

COMMENT ON COLUMN ACTIVITIES.NAME     IS 'Name of activity';
COMMENT ON COLUMN ACTIVITIES.URI      IS 'Uri of java class';
COMMENT ON COLUMN ACTIVITIES.PARAMS   IS 'Activity parameters';
COMMENT ON COLUMN ACTIVITIES.INS_DATE IS 'Insert date time';
COMMENT ON COLUMN ACTIVITIES.UPD_DATE IS 'Update date time';

COMMENT ON TABLE JOBS IS 'Jobs table';

COMMENT ON COLUMN JOBS.ID             IS 'Job identifier';
COMMENT ON COLUMN JOBS.ACTIVITY       IS 'Activity name';
COMMENT ON COLUMN JOBS.PARAMS         IS 'Job parameters';
COMMENT ON COLUMN JOBS.EXPRESSION     IS 'Cron expression';
COMMENT ON COLUMN JOBS.LAST_EXECUTION IS 'Time last execution';
COMMENT ON COLUMN JOBS.LAST_RESULT    IS 'Last result';
COMMENT ON COLUMN JOBS.LAST_ERROR     IS 'Last error';
COMMENT ON COLUMN JOBS.ELAPSED        IS 'Last time elapsed';
COMMENT ON COLUMN JOBS.INS_DATE       IS 'Insert date time';
COMMENT ON COLUMN JOBS.UPD_DATE       IS 'Update date time';

-- Check
--
-- SELECT * FROM ALL_TABLES     WHERE OWNER=USER
-- SELECT * FROM ALL_INDEXES    WHERE OWNER=USER
-- SELECT * FROM ALL_VIEWS      WHERE OWNER=USER
-- SELECT * FROM ALL_TRIGGERS   WHERE OWNER=USER
-- SELECT * FROM ALL_PROCEDURES WHERE OWNER=USER
