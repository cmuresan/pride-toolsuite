package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.mol.IsoelectricPointUtils;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Abstract class of protein table model
 * <p/>
 * User: rwang
 * Date: 24/08/2011
 * Time: 16:21
 */
public class AbstractProteinTableModel extends ProgressiveListTableModel<Void, Tuple<TableContentType, Object>> {


    /**
     * table column title
     */
    public enum TableHeader {
        PROTEIN_ACCESSION_COLUMN("Protein", "Protein Accession"),
        PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
        PROTEIN_STATUS("Status", "Status Of The Protein Accession"),
        PROTEIN_SEQUENCE_COVERAGE("Coverage", "Protein Sequence Coverage"),
        THEORITICAL_ISOELECTRIC_POINT_COLUMN("pI", "Theoritical isoelectric point"),
        //IDENTIFICATION_SCORE_COLUMN("Score", "PRIDE Protein Score"),
        IDENTIFICATION_THRESHOLD_COLUMN("Threshold", "PRIDE Protein Threshold"),
        NUMBER_OF_PEPTIDES("# Peptides", "Number of Peptides"),
        NUMBER_OF_UNIQUE_PEPTIDES("# Distinct Peptides", "Number of Distinct Peptides"),
        NUMBER_OF_PTMS("# PTMs", "Number of PTMs"),
        IDENTIFICATION_ID("Identification ID", "Identification ID"),
        ADDITIONAL("More", "Additional Details");

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

    private Collection<CvTermReference> listScores;

    public AbstractProteinTableModel(Collection<CvTermReference> listScores) {
        this.listScores = listScores;
        addAdditionalColumns();
    }

    void addAdditionalColumns() {
        // add columns for search engine scores
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
            if (listScores != null && TableHeader.IDENTIFICATION_ID.getHeader().equals(header.getHeader())) {
                for (CvTermReference scoreCvTerm : listScores) {
                    String name = scoreCvTerm.getName();
                    columnNames.put(name, name);
                }
            }
        }
    }

    @Override
    public void initializeTableModel() {
       /* TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }     */
        // nothing to do here
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PROTEIN_DETAILS.equals(type)) {
            addProteinDetailData(newData.getValue());
        } else if (TableContentType.PROTEIN_SEQUENCE_COVERAGE.equals(type)) {
            addSequenceCoverageData(newData.getValue());
        }
    }

    /**
     * Add protein detail data
     *
     * @param newData protein detail map
     */
    void addProteinDetailData(Object newData) {

        // column index for mapped protein accession column
        int mappedAccIndex = getColumnIndex(TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader());
        // column index for protein name
        int identNameIndex = getColumnIndex(TableHeader.PROTEIN_NAME.getHeader());
        // column index for protein status
        int identStatusIndex = getColumnIndex(TableHeader.PROTEIN_STATUS.getHeader());
        // column index for isoelectric point
        int isoelectricIndex = getColumnIndex(TableHeader.THEORITICAL_ISOELECTRIC_POINT_COLUMN.getHeader());

        // get a map of protein accession to protein details
        Map<String, Protein> proteins = (Map<String, Protein>) newData;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = (List<Object>)contents.get(row);
            Object proteinAccession = content.get(mappedAccIndex);
            if (proteinAccession != null) {
                String mappedAccession = ((ProteinAccession) proteinAccession).getMappedAccession();
                if (mappedAccession != null) {
                    Protein protein = proteins.get(mappedAccession);
                    if (protein != null) {
                        protein = new AnnotatedProtein(proteins.get(mappedAccession));
                        // set protein name
                        content.set(identNameIndex, protein.getName());
                        // set protein status
                        content.set(identStatusIndex, protein.getStatus().name());
                        // set isoelectric point
                        String sequence = protein.getSequenceString();
                        content.set(isoelectricIndex, sequence == null ? null : IsoelectricPointUtils.calculate(sequence));
                        // notify a row change
                        fireTableRowsUpdated(row, row);
                    }
                }
            }
        }
    }

    /**
     * Add protein sequence coverages
     *
     * @param newData sequence coverage map
     */
    void addSequenceCoverageData(Object newData) {
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        // column index for protein sequence coverage
        int coverageIndex = getColumnIndex(TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());

        // map contains sequence coverage
        Map<Comparable, Double> coverageMap = (Map<Comparable, Double>) newData;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = (List<Object>)contents.get(row);
            Object identId = content.get(identIdIndex);
            Double coverage = coverageMap.get(identId);
            if (coverage != null) {
                // set protein name
                content.set(coverageIndex, coverage);
                // notify a row change
                fireTableCellUpdated(row, coverageIndex);
            }
        }
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;

        if (!contents.isEmpty() && rowIndex >= 0 && columnIndex >= 0) {
            List<Object> colValues = (List<Object>)contents.get(rowIndex);
            if (colValues != null) {
                result = colValues.get(columnIndex);
            }
        }

        return result;
    }
}