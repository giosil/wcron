--
-- Functions
--

CREATE OR REPLACE FUNCTION ENCRYPT_TEXT(vcText IN VARCHAR2) RETURN VARCHAR2 IS
vcResult VARCHAR2(2048);
vcKey    VARCHAR2(32);
BEGIN
  vcResult := NULL;
  vcKey    := 'A!190j2#Az10?#!@197AOIksu[&#$Y!0';
  dbms_obfuscation_toolkit.DESEncrypt(input_string => RPAD(vcText, 48, ' '), key_string => vcKey, encrypted_string => vcResult);
  RETURN vcResult;
  EXCEPTION
    WHEN OTHERS THEN
      RAISE;
END ENCRYPT_TEXT;
/

CREATE OR REPLACE FUNCTION DECRYPT_TEXT(vcText IN VARCHAR2) RETURN VARCHAR2 IS
vcResult VARCHAR2(2048);
vcKey    VARCHAR2(32);
BEGIN
  vcResult := NULL;
  vcKey    := 'A!190j2#Az10?#!@197AOIksu[&#$Y!0';
  dbms_obfuscation_toolkit.DESDecrypt(input_string => vcText, key_string => vcKey, decrypted_string => vcResult);
  RETURN trim(vcResult);
  EXCEPTION
    WHEN OTHERS THEN
      RAISE;
END DECRYPT_TEXT;
/

CREATE OR REPLACE FUNCTION DROP_ALL_SCHEMA_OBJECTS RETURN NUMBER AS PRAGMA AUTONOMOUS_TRANSACTION;
cursor c_get_objects is
  select object_type,'"'||object_name||'"'||decode(object_type,'TABLE',' cascade constraints purge',null) obj_name
  from user_objects
  where object_type in ('TABLE','VIEW','PACKAGE','SEQUENCE','SYNONYM','MATERIALIZED VIEW')
  order by object_type;
cursor c_get_objects_type is
  select object_type, '"'||object_name||'"' obj_name
  from user_objects
  where object_type in ('TYPE');
BEGIN
  begin
    for object_rec in c_get_objects loop
      execute immediate ('drop '||object_rec.object_type||' ' ||object_rec.obj_name);
    end loop;
    for object_rec in c_get_objects_type loop
      begin
        execute immediate ('drop '||object_rec.object_type||' ' ||object_rec.obj_name);
      end;
    end loop;
  end;
  RETURN 0;
END DROP_ALL_SCHEMA_OBJECTS;
/
