set search_path = "public";

DO
'
    DECLARE
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM PATIENT) THEN
            INSERT INTO PATIENT (ID, FIRST_NAME, LAST_NAME)
            VALUES (1, ''John'', ''Smith''),
                   (2, ''Emma'', ''Johnson''),
                   (3, ''Michael'', ''Brown''),
                   (4, ''Olivia'', ''Davis''),
                   (5, ''William'', ''Miller''),
                   (6, ''Ava'', ''Wilson''),
                   (7, ''James'', ''Moore''),
                   (8, ''Sophia'', ''Taylor''),
                   (9, ''Benjamin'', ''Anderson''),
                   (10, ''Isabella'', ''Thomas''),
                   (11, ''Lucas'', ''Jackson''),
                   (12, ''Mia'', ''White''),
                   (13, ''Henry'', ''Harris''),
                   (14, ''Amelia'', ''Martin''),
                   (15, ''Alexander'', ''Thompson'');
            UPDATE APP_SEQ_GENERATOR
            SET SEQ_VALUE = 16
            WHERE SEQ_NAME = ''PATIENT_SEQ_PK'';
        END IF;
    END;
' LANGUAGE PLPGSQL;
