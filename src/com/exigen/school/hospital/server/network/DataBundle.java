package com.exigen.school.hospital.server.network;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 04.11.13
 */
public class DataBundle implements Serializable {
    private int columnNumber;
    private String[] columnNames;
    private List<List<Object>> rows;
    private int[] columnTypes;
    private boolean[] columnWritableFlags;
    private String tableName;

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<List<Object>> getRows() {
        return rows;
    }

    public void setRows(List<List<Object>> rows) {
        this.rows = rows;
    }

    public int[] getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(int[] columnTypes) {
        this.columnTypes = columnTypes;
    }

    public boolean[] getColumnWritableFlags() {
        return columnWritableFlags;
    }

    public void setColumnWritableFlags(boolean[] columnWritableFlags) {
        this.columnWritableFlags = columnWritableFlags;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
