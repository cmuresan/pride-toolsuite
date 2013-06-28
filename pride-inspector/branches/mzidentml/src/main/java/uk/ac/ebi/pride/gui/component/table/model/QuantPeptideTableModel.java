package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.term.CvTermReference;

import java.util.Collection;
import java.util.List;

/**
 * Quantitative peptide table model
 *
 * User: rwang
 * Date: 11/08/2011
 * Time: 13:34
 */
public class QuantPeptideTableModel extends AbstractPeptideTableModel {

    public QuantPeptideTableModel(Collection<CvTermReference> listScores) {
        super(listScores);
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PEPTIDE_QUANTITATION_HEADER.equals(type)) {
            setHeaders(newData.getValue());
        } else if (TableContentType.PEPTIDE_QUANTITATION.equals(type)) {
            addPeptideData((PeptideTableRow)newData.getValue());
        } else {
            super.addData(newData);
        }
    }

    @SuppressWarnings("unchecked")
    private void setHeaders(Object value) {
        // clear all the columns
        columnNames.clear();
        // add fixed columns
        addAdditionalColumns();

        List<String> hs = (List<String>)value;
        for (String h : hs) {
            columnNames.put(h, h);
        }

        fireTableStructureChanged();
    }

    /**
     * Add peptide row data
     *
     * @param peptideTableRow peptide table row
     */
    private void addPeptideData(PeptideTableRow peptideTableRow) {
        int rowCnt = this.getRowCount();
        this.addRow(peptideTableRow);
        fireTableRowsInserted(rowCnt, rowCnt);
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PeptideTableRow peptideTableRow = (PeptideTableRow)contents.get(rowIndex);

        int additionalColumnIndex = getColumnIndex(PeptideTableHeader.ADDITIONAL.getHeader());

        if (columnIndex > additionalColumnIndex) {
            // quantification columns will always be at the end of the table
            List<Object> quantifications = peptideTableRow.getQuantifications();
            return quantifications.get(columnIndex - additionalColumnIndex);
        } else {
            return super.getValueAt(rowIndex, columnIndex);
        }
    }
}