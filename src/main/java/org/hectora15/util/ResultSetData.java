package org.hectora15.util;

import java.util.List;

public class ResultSetData {

    public List<String> columnNames;
    public List<List<String>> rows;

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