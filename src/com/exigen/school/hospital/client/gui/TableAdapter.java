package com.exigen.school.hospital.client.gui;

import com.exigen.school.hospital.client.gui.exceptions.ServerResponseException;
import com.exigen.school.hospital.client.network.ClientMessage;
import com.exigen.school.hospital.client.network.ClientMessageTypes;
import com.exigen.school.hospital.client.network.NetworkConfig;
import com.exigen.school.hospital.client.network.NetworkDriver;
import com.exigen.school.hospital.server.network.DataBundle;
import com.exigen.school.hospital.server.network.ErrorBundle;
import com.exigen.school.hospital.server.network.ServerMessage;
import com.exigen.school.hospital.server.network.ServerMessageTypes;
import com.exigen.school.hospital.server.storage.jdbc.JdbcConfig;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;


/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 30.10.13
 */


public class TableAdapter extends AbstractTableModel implements JdbcConfig, NetworkConfig {
    final Logger logger;
    NetworkDriver connection;
    ServerMessage response;

    //This is used as a component parameter in JOptionPane.showMessage()
    Component context;

    String[] columnNames = {};
    List<List<Object>> rows = new ArrayList<List<Object>>();
    int[] columnTypes = {};
    boolean[] columnWritableFlags = {};
    private String tableName;

    //ResultSetMetaData metaData;
    boolean isEditable = false;

    //this is part of table updating mechanism
    ClientMessage lastExecutedMessage;

    public TableAdapter() {
        logger = Logger.getLogger(this.getClass().getName());
        logger.setLevel(NetworkConfig.LOG_LEVEL);
        try {
            logger.log(Level.INFO, "Getting network driver instance");
            connection = NetworkDriver.getInstance();
        } catch (IOException e) {
            showUserMessage(e);
        } catch (ClassNotFoundException e) {
            showUserMessage(e);
        }
    }

    public void setContext(Component context) {
        this.context = context;
    }

    /**
     * Updates table data when user clicks on the Refresh button
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void update() {
        if (lastExecutedMessage == null) {
            return;
        }

        try {
            response = connection.sendMessage(lastExecutedMessage);
            parseServerResponse();
        } catch (ServerResponseException e) {
            showUserMessage(e);
        } catch (IOException e) {
            showUserMessage(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    public String getSelectedRowId(JTable table) {
        int selectedRowIndex = table.getSelectedRow();
        return String.valueOf(getValueAt(selectedRowIndex, 0));
    }

    public void deleteRow(JTable table) throws ServerResponseException {
        //int selectedRowIndex = table.getSelectedRow();
        Map<String, String> params = new HashMap<String, String>();
        params.put("tableName", tableName);
        System.out.println(tableName);
        params.put(ID_FIELD_NAME, getSelectedRowId(table));

        try {
            executeQuery(ClientMessageTypes.DELETE, params);
        } catch (IOException e) {
            showUserMessage(e);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } catch (ServerResponseException e) {
            logger.log(Level.SEVERE, e.getMessage() + "error code: " + e.getErrorCode());
            throw e;
        }

        update();
        table.removeColumn(table.getColumnModel().getColumn(0));
    }

    public void executeQuery(ClientMessageTypes type, Map<String, String> params)
            throws IOException, ClassNotFoundException, ServerResponseException {

        ClientMessage message = new ClientMessage();
        message.setType(type);
        message.setParams(params);
        response = connection.sendMessage(message);
        parseServerResponse();

        //storing client message for table data updating mechanism
        //we do not need to store update or add massages
        if (type != ClientMessageTypes.UPDATE_CELL &&
                type != ClientMessageTypes.ADD &&
                type != ClientMessageTypes.DELETE &&
                type != ClientMessageTypes.UPDATE_ROW) {
            lastExecutedMessage = message;
        }

    }

    public void parseServerResponse() throws ServerResponseException {
        ServerMessageTypes type = response.getType();
        switch (type) {
            case QUERY_ERROR:
                if (response.getErrorBundle() == null) {
                    return;
                }
                ErrorBundle errorBundle = response.getErrorBundle();
                String errorMsg = errorBundle.getErrorMsg();
                int errorCode = errorBundle.getErrorCode();
                String clientMsg = errorBundle.getClientMessage()
                        .getType().toString();
                String clientMsgParams = errorBundle.getClientMessage()
                        .getParams().toString();

                throw new ServerResponseException(errorMsg, errorCode);

            case QUERY_OK:
                if (response.getDataBundle() == null) {
                    return;
                }
                DataBundle bundle = response.getDataBundle();
                setTableName(bundle.getTableName());
                columnNames = bundle.getColumnNames();
                rows = bundle.getRows();
                columnTypes = bundle.getColumnTypes();
                columnWritableFlags = bundle.getColumnWritableFlags();
                fireTableChanged(null);
                break;
        }
    }

    public void close() throws IOException {
        System.out.println("Closing socket connection");
        connection.close();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    //////////////////////////////////////////////////////////////////////////
    //
    //             Implementation of the TableModel Interface
    //
    //////////////////////////////////////////////////////////////////////////
    // MetaData
    @Override
    public String getColumnName(int column) {
        if (columnNames[column] != null) {
            return columnNames[column];
        } else {
            return "";
        }

    }

    @Override
    public Class<?> getColumnClass(int column) {

        int type = columnTypes[column];

        switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return String.class;

            case Types.BIT:
                return Boolean.class;

            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                return Integer.class;

            case Types.BIGINT:
                return Long.class;

            case Types.FLOAT:
            case Types.DOUBLE:
                return Double.class;

            case Types.DATE:
                return java.sql.Date.class;

            default:
                return Object.class;
        }
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        /*return isEditable;*/
        if (!isEditable) {
            return false;
        }
        return columnWritableFlags[column];
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    // Data methods
    public int getRowCount() {
        return rows.size();
    }

    public Object getValueAt(int aRow, int aColumn) {
        List<Object> row = rows.get(aRow);
        return row.get(aColumn);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

        Map<String, String> params = new HashMap<String, String>();

        params.put("tableName", getTableName());
        params.put("columnName", getColumnName(column));
        if (value instanceof String) {
            params.put("value", (String) value);
        } else if (value instanceof Integer) {
            params.put("value", String.valueOf(value));
        }
        params.put("id", String.valueOf(getValueAt(row, 0)));

        if (JOptionPane.showConfirmDialog
                (context,
                        COMMIT_DIALOG_MESSAGE,
                        "Commit changes",
                        JOptionPane.YES_NO_OPTION) ==
                JOptionPane.NO_OPTION) {
            return;
        }


        try {
            connection = NetworkDriver.getInstance();
            executeQuery(ClientMessageTypes.UPDATE_CELL, params);
            List<Object> dataRow = rows.get(row);
            dataRow.set(column, value);

        } catch (ServerResponseException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            if (e.getMessage().equals(CONNECTION_REFUSED)) {
                JOptionPane.showMessageDialog(context, CONNECTION_FAILED_MESSAGE);
            } else if (e.getMessage().equals(SOCKET_CLOSED)) {
                JOptionPane.showMessageDialog(context, CONNECTION_CLOSED_MESSAGE);
            } else {
                logger.log(Level.SEVERE, e.getMessage());
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void showUserMessage(Throwable t) {
        if (t.getMessage().equals(CONNECTION_REFUSED)) {
            JOptionPane.showMessageDialog(context, CONNECTION_FAILED_MESSAGE);
        } else if (t.getMessage().equals(SOCKET_CLOSED)) {
            JOptionPane.showMessageDialog(context, CONNECTION_CLOSED_MESSAGE);
        } else {
            logger.log(Level.SEVERE, t.getMessage());
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
