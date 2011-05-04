package uk.ac.ebi.pride.gui.component.model;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * Extend this table model to update data.
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 16:46:32
 */
public abstract class UpdateTableModel<T> extends AbstractTableModel {
    protected Map<String, Class> columnNames = null;
    protected List<List<Object>> contents =  null;

    public UpdateTableModel() {
        columnNames = new LinkedHashMap<String, Class>();
        contents = new ArrayList<List<Object>>();
        initializeTable();
    }

    /**
     * This method should be implemented to columns
     * and basic rows of the table
     */
    public abstract void initializeTable();

    /**
     * Add new data into contents.
     * @param newData should be the object returned from long running task
     */
    public abstract void addData(T newData);

    /**
     * Add an extra column to table model
     * @param columnName    column string name
     * @param classType class type for the column
     */
    public void addColumn(String columnName, Class classType) {
        columnNames.put(columnName, classType);
    }

    /**
     * Add an extra row of data to table model
     * @param content   a list of data objects
     */
    public void addRow(List<Object> content) {
        contents.add(content);
    }

    public void removeAllRows() {
        contents.clear();
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
     * @param header column title
     * @return int  index of the column in int
     */
    public int getColumnIndex(String header) {
        int index = -1;

        synchronized(columnNames) {
            List<Map.Entry<String, Class>> entries = new LinkedList<Map.Entry<String, Class>>(columnNames.entrySet());

            for(Map.Entry<String, Class> entry : entries) {
                if (entry.getKey().equals(header)) {
                    index = entries.indexOf(entry);
                }
            }
        }

        return index;
    }

    public String getColumnName(int index) {
        String columnName = null;
        synchronized(columnNames) {
            List<Map.Entry<String, Class>> entries = new LinkedList<Map.Entry<String, Class>>(columnNames.entrySet());

            Map.Entry<String, Class> entry = entries.get(index);
            if (entry != null) {
                columnName = entry.getKey();
            }
        }
        return columnName;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        synchronized(contents) {
            if (!contents.isEmpty() && rowIndex >=0 && columnIndex >=0 ) {
                List<Object> colValues = contents.get(rowIndex);
                if (colValues != null) {
                    result = colValues.get(columnIndex);
                }
            }
        }
        return result;
    }
}
