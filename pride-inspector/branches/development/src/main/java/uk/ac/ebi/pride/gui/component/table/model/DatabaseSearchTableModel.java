package uk.ac.ebi.pride.gui.component.table.model;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Table model for database search table
 * <p/>
 * User: rwang
 * Date: 02/06/11
 * Time: 15:23
 */
public class DatabaseSearchTableModel extends ListTableModel<List<String>> {
    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("Row", "Row Number"),
        SELECTED("Selected", "Selected experiment"),
        VIEW("View", "View experiment");

        private final String header;
        private final String toolTip;

        private TableHeader(String header, String tooltip) {
            this.header = header;
            this.toolTip = tooltip;
        }

        public String getHeader() {
            return header;
        }

        public String getToolTip() {
            return toolTip;
        }
    }

    public DatabaseSearchTableModel() {
        // enable annotation
        AnnotationProcessor.process(this);
    }

    @Override
    public void initializeTableModel() {
        for (TableHeader tableHeader : TableHeader.values()) {
            addColumn(tableHeader.getHeader(), tableHeader.getToolTip());
        }
    }

    @Override
    public void addData(List<String> newData) {
        List<Object> content = new ArrayList<Object>();
        // row number
        int rowCnt = this.getRowCount();
        content.add(rowCnt + 1);
        content.add(new Boolean(false));
        content.add(newData.get(0));
        content.addAll(newData);
        contents.add(content);

        fireTableRowsInserted(rowCnt, rowCnt);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        String columnName = getColumnName(columnIndex);
        if (columnName.equals(TableHeader.SELECTED.getHeader())) {
            return Boolean.class;
        } else {
            return super.getColumnClass(columnIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        int index = getColumnIndex(TableHeader.SELECTED.getHeader());
        return index == columnIndex;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        List<Object> content = contents.get(rowIndex);
        if (content != null) {
            content.set(columnIndex, aValue);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Get all the values of search results
     * Note: the result excludes the the row number, selection column and view column
     *
     * @return  List<List<Object>>  all the actual values in the table
     */
    public List<List<String>> getAllContent() {
        List<List<String>> results = new ArrayList<List<String>>();
        for (List<Object> content : contents) {
            List<String> result = new ArrayList<String>();
            for (int i = TableHeader.values().length; i < content.size(); i++) {
                result.add(content.get(i).toString());
            }
            results.add(result);
        }
        return results;
    }

    /**
     * Get all the headers of the table without the row number, selection column and view column
     *
     * @return  List<String>    a list of headers
     */
    public List<String> getAllHeaders() {
        List<String> headers = new ArrayList<String>();
        int cnt = this.getColumnCount();
        for (int i = 3; i < cnt; i++) {
            headers.add(this.getColumnName(i));
        }
        return headers;
    }

    @EventSubscriber(eventClass = DatabaseSearchEvent.class)
    public void onDatabaseSearchEvent(DatabaseSearchEvent evt) {
        if (DatabaseSearchEvent.Status.RESULT.equals(evt.getStatus())) {
            List<List<String>> newData = (List<List<String>>) evt.getResult();
            for (List<String> data : newData) {
                addData(data);
            }
        } else if (DatabaseSearchEvent.Status.HEADER.equals(evt.getStatus())) {
            List<String> headers = (List<String>) evt.getResult();
            for (String header : headers) {
                addColumn(header, header);
            }
            fireTableStructureChanged();
        }
    }
}
