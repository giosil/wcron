--
-- Data script of schema WCRON
--

INSERT INTO ACTIVITIES(NAME,URI,PARAMS,INS_DATE) VALUES('demo','mock','{"greeting":"hello"}','2020-03-16 00:00:00');

INSERT INTO JOBS(ACTIVITY,EXPRESSION,INS_DATE) VALUES('demo','1000 5000','2020-03-16 00:00:00');

