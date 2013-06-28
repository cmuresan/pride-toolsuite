package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.term.CvTermReference;

import java.util.Collection;

/**
 * IdentificationTableModel stores all information to be displayed in the identification table.
 * <p/>
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 15:58:04
 */
public class ProteinTableModel extends AbstractProteinTableModel {

    public ProteinTableModel(Collection<CvTermReference> listProteinScores) {
        super(listProteinScores);
    }

    /*@Override
    public void initializeTableModel() {
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }   */

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
        int rowCnt = this.getRowCount();
        // row number
        contents.add(newData);
        fireTableRowsInserted(rowCnt, rowCnt);
    }
}
