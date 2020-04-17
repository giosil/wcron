--
-- Drop tables
--

DROP TABLE JOBS       CASCADE CONSTRAINTS PURGE;

DROP TABLE ACTIVITIES CASCADE CONSTRAINTS PURGE;

DROP TABLE LOG_DATA   CASCADE CONSTRAINTS PURGE;

-- Drop sequences

DROP SEQUENCE SEQ_JOBS;

-- Drop triggers (unnecessary because already dropped with DROP TABLE)

-- DROP TRIGGER TRG_JOBS_AUTOINC_ID;

-- DROP TRIGGER TRG_ACTIVITIES;

-- DROP TRIGGER TRG_JOBS;

-- Drop functions

DROP FUNCTION DECRYPT_TEXT;

DROP FUNCTION ENCRYPT_TEXT;

DROP FUNCTION DROP_ALL_SCHEMA_OBJECTS;

-- Drop procedures

DROP PROCEDURE CLEAN_DATA;

-- Drop views

DROP VIEW V_JOBS;

