package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Triple;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 11/08/2011
 * Time: 09:17
 */
public class ProteinQuantTableModel extends ProgressiveListTableModel<Void, Tuple<TableContentType, Object>> {

    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("#", "Row Number"),
        COMPARE("Compare", "Click to choose the protein you want to compare"),
        PROTEIN_ACCESSION_COLUMN("Submitted", "Submitted Protein Accession From Source"),
        MAPPED_PROTEIN_ACCESSION_COLUMN("Mapped", "Pride Mapped Protein Accession"),
        PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
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

    /**
     * mapping between protein identification id and row number
     */
    Map<Comparable, Integer> identIdToRowNumMapping;

    public ProteinQuantTableModel() {
        this.identIdToRowNumMapping = new HashMap<Comparable, Integer>();
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

        if (TableContentType.PROTEIN_QUANTITATION_HEADER.equals(type)) {
            setHeaders(newData.getValue());
        } else if (TableContentType.PROTEIN_QUANTITATION.equals(type)) {
            addProteinQuantData(newData.getValue());
        } else if (TableContentType.PROTEIN_DETAILS.equals(type)) {
            addProteinDetailData(newData.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private void setHeaders(Object value) {
        // clear all the columns
        columnNames.clear();
        // add fixed columns
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }

        List<String> hs = (List<String>)value;
        for (String h : hs) {
            columnNames.put(h, h);
        }

        fireTableStructureChanged();
    }

    @SuppressWarnings("unchecked")
    private void addProteinQuantData(Object value) {
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        int compareColIndex = getColumnIndex(TableHeader.COMPARE.getHeader());

        List<Object> data = (List<Object>) value;
        // get the ident id
        Comparable identId = (Comparable)data.get(identIdIndex - 2);
        Integer rowNum = identIdToRowNumMapping.get(identId);
        if (rowNum == null) {
            // full row
            List<Object> content = new ArrayList<Object>();
            rowNum = this.getRowCount();
            // row number
            content.add(rowNum + 1);
            content.add(false);
            content.addAll(data);
            contents.add(content);
            identIdToRowNumMapping.put(identId, rowNum);
        } else {
            List<Object> content = contents.get(rowNum);
            // remove previous quant data
            for (int i = compareColIndex + 1; i < content.size(); i++) {
                content.remove(i);
            }
            // add new quant data
            content.addAll(data);
        }
        fireTableRowsUpdated(rowNum, rowNum);
    }

    @SuppressWarnings("unchecked")
    private void addProteinDetailData(Object value) {
        // column index for mapped protein accession column
        int mappedAccIndex = getColumnIndex(TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader());
        // column index for protein name
        int identNameIndex = getColumnIndex(TableHeader.PROTEIN_NAME.getHeader());

        // get a map of protein accession to protein details
        Map<String, Protein> proteins = (Map<String, Protein>) value;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = contents.get(row);
            String mappedAcc = (String)content.get(mappedAccIndex);
            if (mappedAcc != null) {
                Protein protein = proteins.get(mappedAcc);
                if (protein != null) {
                    protein = new AnnotatedProtein(proteins.get(mappedAcc));
                    // set protein name
                    content.set(identNameIndex, protein.getName());
                    // notify a row change
                    fireTableRowsUpdated(row, row);
                }
            }
        }
    }
}
