package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.SearchEngine;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Quantitative peptide table model
 *
 * User: rwang
 * Date: 11/08/2011
 * Time: 13:34
 */
public class QuantPeptideTableModel extends AbstractPeptideTableModel {

    public QuantPeptideTableModel(SearchEngine se, DataAccessController controller) {
        super(se, controller);
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PEPTIDE_QUANTITATION_HEADER.equals(type)) {
            setHeaders(newData.getValue());
        } else if (TableContentType.PEPTIDE_QUANTITATION.equals(type)) {
            addPeptideData(newData.getValue());
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
     * @param newData peptide data
     */
    private void addPeptideData(Object newData) {
        int rowCnt = this.getRowCount();
        List<Object> content = new ArrayList<Object>();
        // add row number
        content.add(rowCnt + 1);
        // add the rest content
        content.addAll((List<String>) newData);
        this.addRow(content);
        fireTableRowsInserted(rowCnt, rowCnt);
    }
}