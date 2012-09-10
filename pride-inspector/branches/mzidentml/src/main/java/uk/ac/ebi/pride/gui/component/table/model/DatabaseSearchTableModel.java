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
public class DatabaseSearchTableModel extends ListTableModel<List<Object>> {
    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("#", "Row Number"),
        VIEW("View", "View experiment"),
        EXPERIMENT_ACCESSION("Accession", "PRIDE Experiment Accession"),
        EXPERIMENT_TITLE("Title", "PRIDE Experiment Title"),
        PROJECT("Project", "PRIDE Project Name"),
        SPECIES("Species", "Sample Species"),
        TAXONOMY_ID("Taxonomy ID", "Sample Taxonomy ID"),
        TISSUE("Tissue", "Sample Tissue"),
        BRENDA_ID("BRENDA ID (Tissue)", "Tissue's BRENDA ID"),
        PTM("PTM", "Post Translational Modifications"),
        NUMBER_OF_SPECTRA("#Spectra", "Number of spectra"),
        NUMBER_OF_PROTEIN("#Proteins", "Number of proteins"),
        NUMBER_OF_PEPTIDE("#Peptides", "Number of peptides"),
        REFERENCE("Reference", "Full Reference Line"),
        PUBMED_ID("PubMed ID", "PubMed ID");

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
    public void addData(List<Object> newData) {
        List<Object> content = new ArrayList<Object>();
        // row number
        int rowCnt = this.getRowCount();
        content.add(rowCnt + 1);
        content.add(newData.get(0));
        content.addAll(newData);
        contents.add(content);

        fireTableRowsInserted(rowCnt, rowCnt);
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
     * @return List<List<Object>>  all the actual values in the table
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
     * Get all the headers of the table without the row number, view column
     *
     * @return List<String>    a list of headers
     */
    public List<String> getAllHeaders() {
        List<String> headers = new ArrayList<String>();
        int cnt = this.getColumnCount();
        for (int i = 2; i < cnt; i++) {
            headers.add(this.getColumnName(i));
        }
        return headers;
    }

    @EventSubscriber(eventClass = DatabaseSearchEvent.class)
    public void onDatabaseSearchEvent(DatabaseSearchEvent evt) {
        if (DatabaseSearchEvent.Status.RESULT.equals(evt.getStatus())) {
            List<List<Object>> newData = (List<List<Object>>) evt.getResult();
            for (List<Object> data : newData) {
                addData(data);
            }
        }
    }
}
