package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.core.SearchEngine;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.gui.component.sequence.Protein;
import uk.ac.ebi.pride.term.CvTermReference;

import java.util.ArrayList;
import java.util.List;

/**
 * PeptideTableModel contains all the detailed that displayed in peptide table.
 * <p/>
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 15:58:15
 */
public class PeptideTableModel extends ProgressiveUpdateTableModel<Void, Tuple<TableContentType, Object>> {

    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("Row", "Row Number"),
        PEPTIDE_PTM_COLUMN("Peptide Sequence", "Peptide Sequence"),
        PROTEIN_ACCESSION_COLUMN("Submitted Protein Accession", "Submitted Protein Accession From Source"),
        MAPPED_PROTEIN_ACCESSION_COLUMN("Mapped Protein Accession", "Pride Mapped Protein Accession"),
        PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
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
        SPECTRUM_REFERENCE_COLUMN("Spectrum", "Spectrum Reference"),
        IDENTIFICATION_ID_COLUMN("Identification ID", "Identification ID"),
        PEPTIDE_ID_COLUMN("Peptide ID", "Peptide ID");

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

    public PeptideTableModel(SearchEngine se) {
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
        int rowCnt = this.getRowCount();
        if (TableContentType.PEPTIDE.equals(type)) {
            List<Object> content = new ArrayList<Object>();
            // add row number
            content.add(rowCnt + 1);
            // add the rest content
            content.addAll((List<String>) newData.getValue());
            this.addRow(content);
            fireTableRowsInserted(rowCnt, rowCnt);
        } else if (TableContentType.PROTEIN_DETAILS.equals(type)) {

            // get mapped protein accession column index and protein name column index
            int mappedAccIndex = getColumnIndex(TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader());
            int protNameIndex = getColumnIndex(TableHeader.PROTEIN_NAME.getHeader());

            // get forwarded protein name
            Protein protein = (Protein)newData.getValue();
            // iterate over each row, set the protein name
            for (int row = 0; row < contents.size(); row++) {
                List<Object> content = contents.get(row);
                if (content.get(mappedAccIndex) == null) {
                    continue;
                }
                if (content.get(mappedAccIndex).equals(protein.getName())) {
                    content.set(protNameIndex, protein.getName());
                    fireTableCellUpdated(row, protNameIndex);
                }
            }
        }
    }
}
