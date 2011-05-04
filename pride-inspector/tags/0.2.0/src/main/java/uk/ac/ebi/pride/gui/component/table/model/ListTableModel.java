package uk.ac.ebi.pride.gui.component.table.model;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * List based table model, it also keeps track of column name and types.
 * <p/>
 * User: rwang
 * Date: 20-Aug-2010
 * Time: 10:37:00
 */
public abstract class ListTableModel<T> extends AbstractTableModel {
    protected final Map<String, Class> columnNames;
    protected final List<List<Object>> contents;

    public ListTableModel() {
        columnNames = new LinkedHashMap<String, Class>();
        contents = new ArrayList<List<Object>>();
        initializeTableModel();
    }

    /**
     * This method should be implemented to columns
     * and basic rows of the table
     */
    public abstract void initializeTableModel();

    /**
     * Add new data into contents.
     *
     * @param newData should be the object returned from long running task
     */
    public abstract void addData(T newData);

    /**
     * Add an extra column to table model
     *
     * @param columnName column string name
     * @param classType  class type for the column
     */
    public void addColumn(String columnName, Class classType) {
        columnNames.put(columnName, classType);
        //Todo: add notification
    }

    /**
     * Add an extra row of data to table model
     *
     * @param content a list of data objects
     */
    public void addRow(List<Object> content) {
        contents.add(content);
    }

    public void removeAllRows() {
        int rowCnt = contents.size();
        if (rowCnt > 0) {
            contents.clear();
            fireTableRowsDeleted(0, rowCnt - 1);
        }
    }

    @Override
    public int getRowCount() {
        return contents.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    /**
     * Get the index of the column
     *
     * @param header column title
     * @return int  index of the column in int
     */
    public int getColumnIndex(String header) {
        int index = -1;

        List<Map.Entry<String, Class>> entries = new LinkedList<Map.Entry<String, Class>>(columnNames.entrySet());

        for (Map.Entry<String, Class> entry : entries) {
            if (entry.getKey().equals(header)) {
                index = entries.indexOf(entry);
            }
        }

        return index;
    }

    public String getColumnName(int index) {
        String columnName = null;

        List<Map.Entry<String, Class>> entries = new LinkedList<Map.Entry<String, Class>>(columnNames.entrySet());
        Map.Entry<String, Class> entry = entries.get(index);
        if (entry != null) {
            columnName = entry.getKey();
        }

        return columnName;
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;

        if (!contents.isEmpty() && rowIndex >= 0 && columnIndex >= 0) {
            List<Object> colValues = contents.get(rowIndex);
            if (colValues != null) {
                result = colValues.get(columnIndex);
            }
        }

        return result;
    }
}
