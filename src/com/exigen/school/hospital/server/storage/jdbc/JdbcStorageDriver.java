package com.exigen.school.hospital.server.storage.jdbc;

import com.exigen.school.hospital.client.network.ClientMessage;
import com.exigen.school.hospital.client.network.ClientMessageTypes;
import com.exigen.school.hospital.server.network.DataBundle;
import com.exigen.school.hospital.server.network.ErrorBundle;
import com.exigen.school.hospital.server.network.ServerMessage;
import com.exigen.school.hospital.server.network.ServerMessageTypes;
import com.exigen.school.hospital.server.storage.StorageDriver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 28.10.13
 */


public class JdbcStorageDriver implements StorageDriver, JdbcConfig {
    private static JdbcStorageDriver instance = null;
    private final JdbcConnection connection;
    private Logger logger = null;

    public static JdbcStorageDriver getInstance() throws ClassNotFoundException, SQLException {
        return (instance != null) ? instance :
                new JdbcStorageDriver();
    }

    private JdbcStorageDriver() throws ClassNotFoundException, SQLException {
        if (logger == null) {
            logger = Logger.getLogger(this.getClass().getName());
            logger.setLevel(LOG_LEVEL);
        }


        connection = JdbcConnection.getInstance();

    }

    private DataBundle parseResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            return null;
        }

        int numberOfColumns = 0;
        String[] columnNames = {};
        List<List<Object>> rows = new ArrayList<List<Object>>();
        int[] columnTypes = {};
        boolean[] columnWritableFlags = {};
        String tableName = null;

        try {

            ResultSetMetaData metaData = resultSet.getMetaData();
            tableName = metaData.getTableName(1);
            numberOfColumns = metaData.getColumnCount();
            columnNames = new String[numberOfColumns];
            columnTypes = new int[numberOfColumns];
            columnWritableFlags = new boolean[numberOfColumns];

            for (int column = 0; column < numberOfColumns; column++) {
                columnNames[column] = metaData.getColumnLabel(column + 1);
                columnTypes[column] = metaData.getColumnType(column + 1);
                columnWritableFlags[column] = metaData.isWritable(column + 1);
            }

            rows = new ArrayList<List<Object>>();

            while (resultSet.next()) {
                List<Object> newRow = new ArrayList<Object>();
                for (int i = 1; i <= columnNames.length; i++) {
                    newRow.add(resultSet.getObject(i));
                }
                rows.add(newRow);
            }


        } catch (SQLException ex) {
            System.err.println(ex);
        }

        DataBundle bundle = new DataBundle();
        bundle.setTableName(tableName);
        bundle.setColumnNumber(numberOfColumns);
        bundle.setColumnNames(columnNames);
        bundle.setRows(rows);
        bundle.setColumnTypes(columnTypes);
        bundle.setColumnWritableFlags(columnWritableFlags);

        return bundle;
    }

    ServerMessage createErrorMessage(ErrorBundle errorBundle,
                                     ServerMessageTypes type) {
        ServerMessage message = new ServerMessage(type);
        message.seteBundle(errorBundle);
        return message;
    }

    ServerMessage createMessageResultSet(ResultSet rSet, ServerMessageTypes type) {
        ServerMessage message = new ServerMessage(type);
        message.setdBundle(parseResultSet(rSet));
        return message;
    }

    private String createQuery(ClientMessage msg) {
        String query = "";
        switch (msg.getType()) {

            case GET:
                query += GET_ALL_QUERY;
                Map<String, String> params = msg.getParams();
                String tableName = params.get("tableName");
                query += tableName;

                if (params.get("All") != null) {
                    break;
                }

                String operator = " where ";
                String[] searchableFields = null;
                if (tableName.equals(DOCTORS_TABLE_NAME)) {
                    searchableFields = SEARCHABLE_DOCTOR_FIELDS;
                } else if (tableName.equals(PATIENTS_TABLE_NAME)) {
                    searchableFields = SEARCHABLE_PATIENT_FIELDS;
                }
                for (String field : searchableFields) {
                    if (params.containsKey(field)) {
                        if (operator.equals(" where ")) {
                            query += operator;
                            operator = " and ";
                        } else {
                            query += operator;
                        }
                        query += field;
                        query += "=";
                        if (field.equals(ROOM_FIELD_NAME) ||
                                field.equals(SECTOR_FIELD_NAME)) {
                            query += params.get(field);
                        } else {
                            query += "\'" + params.get(field) + "\'";
                        }
                    }
                }
                break;
            case GET_REG:
                query += GET_REGS_QUERY;
                params = msg.getParams();
                operator = " where ";
                if (params.containsKey(ID_FIELD_NAME)) {
                    query += operator;
                    String idAlias = params.get(ID_FIELD_NAME);
                    query += idAlias + "=";
                    query += params.get(idAlias);
                    operator = " and ";
                }
                if (params.containsKey(REG_DATE_FIELD)) {
                    query += operator;
                    query += REGCARD_DATE_ALIAS + "=";
                    query += '\'' + params.get(REG_DATE_FIELD) + '\'';
                }

                break;

            case UPDATE_CELL:
                params = msg.getParams();
                String columnName = params.get("columnName");
                query += "update " + params.get("tableName") +
                        " set " + columnName + "=";
                if (columnName.equals(ROOM_FIELD_NAME) ||
                        columnName.equals(SECTOR_FIELD_NAME)) {

                    query += params.get("value");
                } else {
                    query += "\'" + params.get("value") + "\'";
                }
                query += " where id=" + params.get("id");
                break;

            case UPDATE_ROW:
                params = msg.getParams();
                tableName = params.get("tableName");
                String[] fields = {};
                if (tableName.equals(DOCTORS_TABLE_NAME)) {
                    fields = SEARCHABLE_DOCTOR_FIELDS;
                } else if (tableName.equals(PATIENTS_TABLE_NAME)) {
                    fields = SEARCHABLE_PATIENT_FIELDS;
                }
                query += "update " + tableName + " set ";
                StringBuilder keyValuePair = new StringBuilder();

                for (String field: fields) {
                    if (field.equals(ID_FIELD_NAME))
                        continue;
                    String value = params.get(field);
                    if (value != null) {
                        keyValuePair.append(field + '=');
                        if (field != ROOM_FIELD_NAME && field != SECTOR_FIELD_NAME) {
                            keyValuePair.append('\'' + value + '\'');
                        } else {
                            keyValuePair.append(value);
                        }


                        keyValuePair.append(',');
                    }
                }
                keyValuePair.replace(keyValuePair.length() - 1, keyValuePair.length(), " ");
                keyValuePair.append("where id=");
                keyValuePair.append(params.get(ID_FIELD_NAME));
                query += keyValuePair;

                break;

            case ADD:
                params = msg.getParams();
                tableName = params.get("tableName");
                fields = null;
                if (tableName.equals(DOCTORS_TABLE_NAME)) {
                    fields = SEARCHABLE_DOCTOR_FIELDS;
                } else if (tableName.equals(PATIENTS_TABLE_NAME)) {
                    fields = SEARCHABLE_PATIENT_FIELDS;
                } else if (tableName.equals(REGCARDS_TABLE_NAME)) {

                    // test if doctor's count limit is achieved
                    if (isDoctorCountLimitAchieved(params.get(DOCTOR_ID_FIELD))) {
                        query = DOCTOR_COUNT_LIMIT_ERROR;
                        break;
                    }
                    fields = SEARCHABLE_REGCARDS_FIELDS;
                }
                query += "insert into " + tableName;
                StringBuilder fieldsPart = new StringBuilder();
                StringBuilder valuesPart = new StringBuilder();
                fieldsPart.append(" (");
                valuesPart.append(" (");
                for (String field : fields) {
                    fieldsPart.append(field);
                    fieldsPart.append(',');
                    String value = params.get(field);
                    if (field != ROOM_FIELD_NAME && field != SECTOR_FIELD_NAME &&
                            field != DOCTOR_ID_FIELD && field != PATIENT_ID_FIELD) {
                        valuesPart.append("\'" + value + "\'");
                    } else {
                        valuesPart.append(value);
                    }
                    valuesPart.append(',');
                }
                fieldsPart.replace(fieldsPart.length() - 1, fieldsPart.length(), ") ");
                valuesPart.replace(valuesPart.length() - 1, valuesPart.length(), ") ");

                query += fieldsPart.toString() + " values " + valuesPart.toString();

                break;

            case DELETE:
                params = msg.getParams();
                tableName = params.get("tableName");
                query = "delete from " + tableName + " where id=" +
                        params.get(ID_FIELD_NAME);

                break;


            //this is for debugging purpose only
            case SQL_QUERY:
                query += msg.getParams().get("SqlQuery");
                break;
        }

        return query;
    }

    private boolean isDoctorCountLimitAchieved(String doctorIdStr) {

        int doctor_id = Integer.parseInt(doctorIdStr);
        String query = GET_DOCTOR_NUM_IN_REGS_QUERY;

        query += "where doctor_id=" + doctor_id;
        query +=" group by doctor_id";

        try {
            ResultSet queryResult = connection.executeQuery(query);

            if (queryResult.next()) {
                return (queryResult.getInt(1) == DOCTOR_COUNT_LIMIT);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, SQL_ERROR + e.getErrorCode() + e.getMessage());
        }
        return false;
    }

    public ServerMessage handleMessage(ClientMessage msg) {

        ErrorBundle errorBundle = new ErrorBundle();
        errorBundle.setClientMessage(msg);

        ClientMessageTypes type = msg.getType();
        switch (type) {
            case DELETE:
            case UPDATE_CELL:
            case UPDATE_ROW:
            case ADD:

                String query = createQuery(msg);

                if (query.equals(DOCTOR_COUNT_LIMIT_ERROR)) {
                    errorBundle.setErrorMsg(DOCTOR_COUNT_LIMIT_ERROR);
                    logger.log(Level.INFO, DOCTOR_COUNT_LIMIT_ERROR);
                    return createErrorMessage(errorBundle, ServerMessageTypes.QUERY_ERROR);
                }
                try {
                    if (connection.execute(query)) {
                        logger.log(Level.INFO, SUCCESSFUL_SQL_QUERY + query);
                        return createMessageResultSet(null, ServerMessageTypes.QUERY_OK);
                    } else {
                        logger.log(Level.SEVERE, ABSENT_DATABASE + query);
                        errorBundle.setErrorMsg(ABSENT_DATABASE + query);
                        return createErrorMessage(errorBundle, ServerMessageTypes.QUERY_ERROR);
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, SQL_ERROR + e.getMessage());
                    errorBundle.setErrorMsg(e.getSQLState() + e.getMessage());
                    errorBundle.setErrorCode(e.getErrorCode());
                    return createErrorMessage(errorBundle, ServerMessageTypes.QUERY_ERROR);
                }


            default:
                ResultSet rSet = null;
                query = createQuery(msg);

                try {
                    rSet = connection.executeQuery(createQuery(msg));
                    if (rSet == null) {
                        errorBundle.setErrorMsg(ABSENT_DATABASE + query);
                        logger.log(Level.SEVERE, ABSENT_DATABASE + query);
                        return createErrorMessage(errorBundle, ServerMessageTypes.QUERY_ERROR);
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, SQL_ERROR + e.getErrorCode() + e.getMessage());
                    errorBundle.setErrorMsg(e.getMessage() + e.getSQLState() + e.getErrorCode());
                    return createErrorMessage(errorBundle, ServerMessageTypes.QUERY_ERROR);
                }

                return createMessageResultSet(rSet, ServerMessageTypes.QUERY_OK);
        }

    }
}
