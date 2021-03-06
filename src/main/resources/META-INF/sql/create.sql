--
-- Tables of schema WCRON
--

CREATE TABLE ACTIVITIES(NAME VARCHAR(50) NOT NULL,URI VARCHAR(100) NOT NULL,PARAMS VARCHAR(4000),INS_DATE TIMESTAMP NOT NULL,UPD_DATE TIMESTAMP,CONSTRAINT PK_ACTIVITIES PRIMARY KEY (NAME));

CREATE TABLE JOBS(ID BIGINT IDENTITY,ACTIVITY VARCHAR(50) NOT NULL,PARAMS VARCHAR(4000),EXPRESSION VARCHAR(50) NOT NULL,LAST_EXECUTION TIMESTAMP,LAST_RESULT VARCHAR(4000),LAST_ERROR VARCHAR(255),ELAPSED INT DEFAULT 0 NOT NULL,INS_DATE TIMESTAMP NOT NULL,UPD_DATE TIMESTAMP);

CREATE INDEX IDX_ACT_URI ON ACTIVITIES(URI);

CREATE INDEX IDX_JOB_ACT ON JOBS(ACTIVITY);

CREATE INDEX IDX_JOB_LEX ON JOBS(LAST_EXECUTION);