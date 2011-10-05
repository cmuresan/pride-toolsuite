package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;

import java.util.ArrayList;
import java.util.List;

/**
 * IdentificationTableModel stores all information to be displayed in the identification table.
 * <p/>
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 15:58:04
 */
public class ProteinTableModel extends AbstractProteinTableModel {

    public ProteinTableModel() {
        this(null);
    }

    public ProteinTableModel(DataAccessController controller) {
        super(controller);
    }

    @Override
    public void initializeTableModel() {
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PROTEIN.equals(type)) {
            addIdentificationData(newData.getValue());
        } else {
            super.addData(newData);
        }
    }

    /**
     * Add identification detail for each row
     *
     * @param newData identification detail
     */
    private void addIdentificationData(Object newData) {
        List<Object> content = new ArrayList<Object>();
        int rowCnt = this.getRowCount();
        // row number
        content.add(rowCnt + 1);
        content.addAll((List<Object>) newData);
        contents.add(content);
        fireTableRowsInserted(rowCnt, rowCnt);
    }
}
