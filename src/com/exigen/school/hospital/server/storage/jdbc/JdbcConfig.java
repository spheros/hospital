package com.exigen.school.hospital.server.storage.jdbc;

import com.exigen.school.hospital.server.ServerConfig;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 04.11.13
 */
public interface JdbcConfig extends ServerConfig {

    //loggers' messages
    public static final String OPEN_DB_CONNECTION = "Opening db connection";
    public static final String CLOSE_DB_CONNECTION = "Closing db connection";
    public static final String INIT_DB_TABLES = "DB tables initialized";
    public static final String ABSENT_DATABASE = "There is no database to execute the query\n";
    public static final String SUCCESSFUL_SQL_QUERY = "Successfully executed query ";
    public static final String SQL_ERROR = "SQL execution error\n";

    public static final String JDBC_DRIVER = "org.h2.Driver";
    public static final String JDBC_DRIVER_PREFIX = "jdbc:h2:";
    public static final String DBNAME = "HospitalDB";
    public static final String CONNECTION_URL = JDBC_DRIVER_PREFIX + DBNAME + ";create=true";
    public static final String USERNAME = null;
    public static final String PASS = null;

    public static final String DOCTOR_ROLE_NAME = "Doctor";
    public static final String PATINENT_ROLE_NAME = "Patient";

    public static final String DOCTORS_TABLE_NAME = "DOCTORS";
    public static final String PATIENTS_TABLE_NAME = "PATIENTS";
    public static final String REGCARDS_TABLE_NAME = "REGCARDS";

    public static final String ID_FIELD_NAME = "ID";

    public static final String SURNAME_FIELD_NAME = "Surname";
    public static final String NAME_FIELD_NAME = "Name";
    public static final String ROOM_FIELD_NAME = "ROOM";
    public static final String SPEC_FIELD_NAME = "Spec";
    public static final String SECTOR_FIELD_NAME = "SECTOR";
    public static final String DIAGNISIS_FIELD_NAME = "Diagnosis";

    public static final String DOCTOR_ID_FIELD = "DOCTOR_ID";
    public static final String PATIENT_ID_FIELD = "PATIENT_ID";
    public static final String REG_DATE_FIELD = "REG_DATE";

    public static final String DOCTOR_ID_ALIAS = "d.id";
    public static final String PATIENT_ID_ALIAS = "p.id";
    public static final String REGCARD_ID_ALIAS = "r.id";
    public static final String REGCARD_DATE_ALIAS = "r.reg_date";


    public static final String[] SEARCHABLE_REGCARDS_FIELDS = {
            DOCTOR_ID_FIELD,
            PATIENT_ID_FIELD,
            REG_DATE_FIELD
    };

    public static final String[] SEARCHABLE_DOCTOR_FIELDS = {
            SURNAME_FIELD_NAME,
            NAME_FIELD_NAME,
            ROOM_FIELD_NAME,
            SPEC_FIELD_NAME
    };

    public static final String[] SEARCHABLE_PATIENT_FIELDS = {
            SURNAME_FIELD_NAME,
            NAME_FIELD_NAME,
            SECTOR_FIELD_NAME,
            DIAGNISIS_FIELD_NAME
    };

    public static final String CREATE_TABLE_DOCTORS_QUERY = "CREATE TABLE IF NOT EXISTS " +
            DOCTORS_TABLE_NAME
            + " (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY AUTO_INCREMENT"
            + "   CONSTRAINT PATIENT_PK PRIMARY KEY, "
            + " SURNAME VARCHAR(32) NOT NULL, "
            + " NAME VARCHAR(32) NOT NULL, "
            + " ROOM INT NOT NULL, "
            + " SPEC VARCHAR(32) NOT NULL) ";

    public static final String CREATE_TABLE_PATIENTS_QUERY = "CREATE TABLE IF NOT EXISTS " +
            PATIENTS_TABLE_NAME
            + " (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY AUTO_INCREMENT"
            + "   CONSTRAINT PATIENT_PK PRIMARY KEY, "
            + " SURNAME VARCHAR(32) NOT NULL, "
            + " NAME VARCHAR(32) NOT NULL, "
            + " SECTOR INT NOT NULL, "
            + " DIAGNOSIS VARCHAR(32) NOT NULL) ";

    public static final String CREATE_TABLE_REGCARDS_QUERY = "CREATE TABLE IF NOT EXISTS " +
            REGCARDS_TABLE_NAME
            + "(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY AUTO_INCREMENT"
            + "   CONSTRAINT REGCARD_PK PRIMARY KEY, "
            + " DOCTOR_ID INT NOT NULL, "
            + " PATIENT_ID INT NOT NULL, "
            + " REG_DATE DATE NOT NULL) ";

    public static final String ADD_REGCARDS_FK_ON_DOCTORS_QUERY = "ALTER TABLE REGCARDS " +
            "ADD FOREIGN KEY(DOCTOR_ID) REFERENCES DOCTORS(ID)";
    public static final String ADD_REGCARDS_FK_ON_PATIENTS_QUERY = "ALTER TABLE REGCARDS " +
            "ADD FOREIGN KEY(PATIENT_ID) REFERENCES PATIENTS(ID)";

    public static final String ADD_UNIQUE_REGCARD_CONSTRAINT_QUERY = "alter table regcards " +
            "add constraint if not exists unique_reg_card unique(doctor_id, patient_id, reg_date)";


    public static final int FOREIGN_KEY_CONSTRAINT_ERROR = 23503;
    public static final int UNIQUE_KEY_CONSTRAINT_ERROR = 23505;

    public static final String GET_ALL_QUERY = "select * from ";

    public static final String GET_REGS_QUERY = "select r.id, p.surname, p.name, p.diagnosis, d.surname, " +
            "d.name, d.spec, r.reg_date from regcards r join doctors d on d.id=r.doctor_id " +
            "join patients p on p.id=r.patient_id";

    public static final String GET_DOCTOR_NUM_IN_REGS_QUERY = "select count(*) as num from regcards ";

    public static final String DOCTOR_COUNT_LIMIT_ERROR = "You cannot register patients to this doctor!\n" +
            "The maximum number of registered patients to this doctor has been achieved!";

    public static final int DOCTOR_COUNT_LIMIT = 5;


}
