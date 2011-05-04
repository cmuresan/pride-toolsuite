package uk.ac.ebi.pride.test;

import uk.ac.ebi.pride.gui.component.table.model.ProgressiveUpdateTableModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 01-Aug-2010
 * Time: 09:49:39
 */
public class TableModelImpl extends ProgressiveUpdateTableModel<String, String> {
    public enum TableHeader {
        COLUMN_ONE("Column one", String.class);

        private final String header;
        private final Class headerClass;

        private TableHeader(String header, Class headerClass) {
            this.header = header;
            this.headerClass = headerClass;
        }

        public String getHeader() {
            return header;
        }

        public Class getHeaderClass() {
            return headerClass;
        }
    }

    @Override
    public void initializeTableModel() {
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            addColumn(header.getHeader(), header.getHeaderClass());
        }
    }

    @Override
    public void addData(String newData) {
        List<Object> content = new ArrayList<Object>();
        content.add(newData);
        contents.add(content);
    }
}
