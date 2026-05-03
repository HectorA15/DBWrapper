package org.hectora15.util;

import java.util.List;

/**
 *  Data structure to hold the results of a SQL query in a way that can be easily used by the UI.
 *  It contains the column names and the rows of data as lists of strings.
 */
public class ResultSetData {

    public List<String> columnNames;
    public List<List<String>> rows;

    /**
     * Constructor. Creates a new ResultSetData object with the specified column names and rows.
     * @param columnNames
     * @param rows
     */
    public ResultSetData(List<String> columnNames, List<List<String>> rows) {
        this.columnNames = columnNames;
        this.rows = rows;
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return columnNames.size();
    }

    public String getColumnName(int index) {
        return columnNames.get(index);
    }

    public String getValue(int row, int col) {
        return rows.get(row).get(col);
    }
}