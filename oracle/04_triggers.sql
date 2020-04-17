--
-- Triggers
--

CREATE OR REPLACE TRIGGER TRG_JOBS_AUTOINC_ID BEFORE INSERT ON JOBS FOR EACH ROW

BEGIN

  SELECT SEQ_JOBS.NEXTVAL
  INTO   :NEW.ID
  FROM   dual;

END TRG_JOBS_AUTOINC_ID;
/

CREATE OR REPLACE TRIGGER TRG_ACTIVITIES AFTER INSERT ON ACTIVITIES FOR EACH ROW
DECLARE

vcUser VARCHAR2(50);

BEGIN

  begin
    SELECT USER
    INTO vcUser
    FROM DUAL;
  exception
  when no_data_found then
    vcUser := '-';
  end;
  
  if(vcUser IS NOT NULL) then
    INSERT INTO LOG_DATA(TABLENAME,DB_USER,PK_VALUE,INS_DATE) VALUES('ACTIVITIES', vcUser, :NEW.NAME, SYSTIMESTAMP);
  end if;

  EXCEPTION
    WHEN OTHERS THEN
      -- Consider logging the error and then re-raise
    RAISE;
END TRG_ACTIVITIES;
/

CREATE OR REPLACE TRIGGER TRG_JOBS AFTER INSERT ON JOBS FOR EACH ROW
DECLARE

vcUser VARCHAR2(50);

BEGIN

  begin
    SELECT USER
    INTO vcUser
    FROM DUAL;
  exception
  when no_data_found then
    vcUser := '-';
  end;
  
  if(vcUser IS NOT NULL) then
    INSERT INTO LOG_DATA(TABLENAME,DB_USER,PK_VALUE,INS_DATE) VALUES('JOBS', vcUser, TO_CHAR(:NEW.ID), SYSTIMESTAMP);
  end if;

  EXCEPTION
    WHEN OTHERS THEN
      -- Consider logging the error and then re-raise
    RAISE;
END TRG_JOBS;
/