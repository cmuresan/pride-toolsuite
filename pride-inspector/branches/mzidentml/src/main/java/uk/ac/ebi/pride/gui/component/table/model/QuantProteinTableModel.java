package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quantitative protein table model
 * <p/>
 * User: rwang
 * Date: 11/08/2011
 * Time: 09:17
 */
public class QuantProteinTableModel extends AbstractProteinTableModel {

    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("#", "Row Number"),
        COMPARE("✓", "Click to choose the protein you want to compare"),
        PROTEIN_ACCESSION_COLUMN("Submitted", "Submitted Protein Accession From Source"),
        MAPPED_PROTEIN_ACCESSION_COLUMN("Mapped", "Pride Mapped Protein Accession"),
        PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
        PROTEIN_STATUS("Protein Status", "Status Of The Protein Accession"),
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

    /**
     * mapping between protein identification id and row number
     */
    Map<Comparable, Integer> identIdToRowNumMapping;

    public QuantProteinTableModel(DataAccessController controller) {
        super(controller);
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
    public Class<?> getColumnClass(int columnIndex) {
        String columnName = getColumnName(columnIndex);
        if (columnName.equals(TableHeader.COMPARE.getHeader())) {
            return Boolean.class;
        } else {
            return super.getColumnClass(columnIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        String columnName = getColumnName(columnIndex);
        return columnName.equals(TableHeader.COMPARE.getHeader()) || super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PROTEIN_QUANTITATION_HEADER.equals(type)) {
            setHeaders(newData.getValue());
        } else if (TableContentType.PROTEIN_QUANTITATION.equals(type)) {
            addProteinQuantData(newData.getValue());
        } else {
            super.addData(newData);
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

        List<String> hs = (List<String>) value;
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
        Comparable identId = (Comparable) data.get(identIdIndex - 2);
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
            fireTableRowsInserted(rowNum, rowNum);
        } else {
            List<Object> content = contents.get(rowNum);
            // replace previous quant data
            int offset = compareColIndex + 1;
            for (int i = offset; i < content.size(); i++) {
                content.set(i, data.get(i - offset));
            }
            // notify
            fireTableRowsUpdated(rowNum, rowNum);
        }
    }
}
