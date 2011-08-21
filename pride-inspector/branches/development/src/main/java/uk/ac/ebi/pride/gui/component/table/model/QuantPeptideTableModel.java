package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.core.SearchEngine;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 11/08/2011
 * Time: 13:34
 */
public class QuantPeptideTableModel extends ProgressiveListTableModel<Void, Tuple<TableContentType, Object>> {

    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("#", "Row Number"),
        PEPTIDE_PTM_COLUMN("Peptide", "Peptide Sequence"),
        PROTEIN_ACCESSION_COLUMN("Submitted", "Submitted Protein Accession From Source"),
        MAPPED_PROTEIN_ACCESSION_COLUMN("Mapped", "Pride Mapped Protein Accession"),
        PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
        PROTEIN_STATUS("Protein Status", "Status Of The Protein Accession"),
        PROTEIN_SEQUENCE_COVERAGE("Sequence Coverage", "Protein Sequence Coverage"),
        PEPTIDE_FIT("Fit In Sequence", "Peptide Sequence Fit In Protein Sequence"),
        PRECURSOR_CHARGE_COLUMN("Precursor Charge", "Precursor Charge"),
        DELTA_MASS_COLUMN("Delta m/z", "Delta m/z [Experimental m/z - Theoretical m/z]"),
        PRECURSOR_MZ_COLUMN("Precursor m/z", "Precursor m/z"),
        PEPTIDE_PTM_NUMBER_COLUMN("# PTMs", "Number of PTMs"),
        PEPTIDE_PTM_MASS_COLUMN("Modified Peptide Sequence", "Peptide Sequence with PTM Mass Differences"),
        PEPTIDE_PTM_SUMMARY("PTM List", "List of All PTM Accessions"),
        NUMBER_OF_FRAGMENT_IONS_COLUMN("# Ions", "Number of Fragment Ions"),
        PEPTIDE_SEQUENCE_LENGTH_COLUMN("Length", "Length"),
        SEQUENCE_START_COLUMN("Start", "Start Position"),
        SEQUENCE_END_COLUMN("Stop", "Stop Position"),
        THEORITICAL_ISOELECTRIC_POINT_COLUMN("pI", "Theoritical isoelectric point"),
        SPECTRUM_ID("Spectrum", "Spectrum Reference"),
        IDENTIFICATION_ID("Identification ID", "Identification ID"),
        PEPTIDE_ID("Peptide ID", "Peptide ID");

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

    private SearchEngine searchEngine;

    public QuantPeptideTableModel(SearchEngine se) {
        this.searchEngine = se;
        addAdditionalColumns();
    }

    @Override
    public void initializeTableModel() {
        // nothing here
    }

    private void addAdditionalColumns() {
        // add columns for search engine scores
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
            if (searchEngine != null && TableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader().equals(header.getHeader())) {
                List<SearchEngineType> types = searchEngine.getSearchEngineTypes();
                for (SearchEngineType type : types) {
                    List<CvTermReference> scoreCvTerms = type.getSearchEngineScores();
                    for (CvTermReference scoreCvTerm : scoreCvTerms) {
                        String name = scoreCvTerm.getName();
                        columnNames.put(name, name);
                    }
                }
            }
        }
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PEPTIDE_QUANTITATION_HEADER.equals(type)) {
            setHeaders(newData.getValue());
        } else if (TableContentType.PEPTIDE_QUANTITATION.equals(type)) {
            addPeptideData(newData.getValue());
        } else if (TableContentType.PROTEIN_DETAILS.equals(type)) {
            addProteinDetails(newData.getValue());
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

    /**
     * Add protein related details
     *
     * @param newData protein detail map
     */
    @SuppressWarnings("unchecked")
    private void addProteinDetails(Object newData) {
        // column index for mapped protein accession column
        int mappedAccIndex = getColumnIndex(TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader());
        // column index for protein name
        int identNameIndex = getColumnIndex(TableHeader.PROTEIN_NAME.getHeader());

        // get a map of protein accession to protein details
        Map<String, Protein> proteins = (Map<String, Protein>) newData;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = contents.get(row);
            String mappedAcc = (String)content.get(mappedAccIndex);
            if (mappedAcc != null) {
                Protein protein = proteins.get(mappedAcc);
                if (protein != null) {
                    AnnotatedProtein annotatedProtein = new AnnotatedProtein(protein);
                    // set protein name
                    content.set(identNameIndex, annotatedProtein.getName());
                    // notify a row change
                    fireTableRowsUpdated(row, row);
                }
            }
        }
    }
}