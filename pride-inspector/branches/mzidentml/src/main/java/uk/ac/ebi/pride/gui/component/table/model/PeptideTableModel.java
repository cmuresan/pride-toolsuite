package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.SearchEngine;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.impl.RetrievePeptideFitTask;
import uk.ac.ebi.pride.gui.task.impl.RetrieveSequenceCoverageTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PeptideTableModel contains all the detailed that displayed in peptide table.
 * <p/>
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 15:58:15
 */
public class PeptideTableModel extends AbstractPeptideTableModel {


    public PeptideTableModel(SearchEngine se, DataAccessController controller) {
        super(se, controller);
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PEPTIDE.equals(type)) {
            addPeptideData(newData.getValue());
        } else {
            super.addData(newData);
        }
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
