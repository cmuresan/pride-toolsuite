package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.gui.component.sequence.Protein;

import java.util.ArrayList;
import java.util.List;

/**
 * IdentificationTableModel stores all information to be displayed in the identification table.
 * <p/>
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 15:58:04
 */
public class ProteinTableModel extends ProgressiveUpdateTableModel<Void, Tuple<TableContentType, Object>> {


    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("Row", "Row Number"),
        PROTEIN_ACCESSION_COLUMN("Submitted Protein Accession", "Submitted Protein Accession From Source"),
        MAPPED_PROTEIN_ACCESSION_COLUMN("Mapped Protein Accession", "Pride Mapped Protein Accession"),
        PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
        PROTEIN_SEQUENCE_COVERAGE("Sequence Coverage", "Protein Sequence Coverage"),
        IDENTIFICATION_SCORE_COLUMN("Score", "PRIDE Protein Score"),
        IDENTIFICATION_THRESHOLD_COLUMN("Threshold", "PRIDE Protein Threshold"),
        NUMBER_OF_PEPTIDES("# Peptides", "Number of Peptides"),
        NUMBER_OF_UNIQUE_PEPTIDES("# Distinct Peptides", "Number of Distinct Peptides"),
        NUMBER_OF_PTMS("# PTMs", "Number of PTMs"),
        IDENTIFICATION_ID("Identification ID", "Identification ID");

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
        int rowCnt = this.getRowCount();
        if (TableContentType.IDENTIFICATION.equals(type)) {
            List<Object> content = new ArrayList<Object>();
            // row number
            content.add(rowCnt + 1);
            content.addAll((List<Object>) newData.getValue());
            contents.add(content);
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
                if (content.get(mappedAccIndex).equals(protein.getAccession())) {
                    content.set(protNameIndex, protein.getName());
                    fireTableCellUpdated(row, protNameIndex);
                }
            }
        }
    }
}
