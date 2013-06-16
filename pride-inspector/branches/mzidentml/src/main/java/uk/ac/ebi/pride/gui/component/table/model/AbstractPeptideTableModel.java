package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for peptide table model
 * <p/>
 * User: rwang
 * Date: 24/08/2011
 * Time: 16:36
 */
public class AbstractPeptideTableModel extends ProgressiveListTableModel<Void, Tuple<TableContentType, Object>> {

    /**
     * table column title
     */
    public enum TableHeader {
        PEPTIDE_COLUMN("Peptide", "Peptide Sequence"),
        RANKING("Ranking", "Ranking"),
        PROTEIN_ACCESSION_COLUMN("Protein", "Protein Accession"),
        PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
        PROTEIN_STATUS("Status", "Status Of The Protein Accession"),
        PROTEIN_SEQUENCE_COVERAGE("Coverage", "Protein Sequence Coverage"),
        PEPTIDE_FIT("Fit", "Peptide Sequence Fit In Protein Sequence"),
        PRECURSOR_CHARGE_COLUMN("Charge", "Precursor Charge"),
        DELTA_MASS_COLUMN("Delta m/z", "Delta m/z [Experimental m/z - Theoretical m/z]"),
        PRECURSOR_MZ_COLUMN("Precursor m/z", "Precursor m/z"),
        PEPTIDE_MODIFICATION_COLUMN("Modifications", "Post translational modifications"),
        NUMBER_OF_FRAGMENT_IONS_COLUMN("# Ions", "Number of Fragment Ions"),
        PEPTIDE_SEQUENCE_LENGTH_COLUMN("Length", "Length"),
        SEQUENCE_START_COLUMN("Start", "Start Position"),
        SEQUENCE_END_COLUMN("Stop", "Stop Position"),
        THEORITICAL_ISOELECTRIC_POINT_COLUMN("pI", "Theoritical isoelectric point"),
        SPECTRUM_ID("Spectrum", "Spectrum Reference"),
        IDENTIFICATION_ID("Identification ID", "Identification ID"),
        PEPTIDE_ID("Peptide ID", "Peptide ID"),
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

    AbstractPeptideTableModel(Collection<CvTermReference> listPeptideScores) {
        this.listScores = listPeptideScores;
        addAdditionalColumns();
    }

    @Override
    public void initializeTableModel() {
        // nothing here
    }

    void addAdditionalColumns() {
        // add columns for search engine scores
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
            if (listScores != null && TableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader().equals(header.getHeader())) {
                //List<SearchEngineType> types = searchEngine.getSearchEngineTypes();
                for (CvTermReference scoreCvTerm : listScores) {
                    //List<CvTermReference> scoreCvTerms = type.getSearchEngineScores();
                    ///for (CvTermReference scoreCvTerm : scoreCvTerms) {
                    String name = scoreCvTerm.getName();
                    columnNames.put(name, name);
                    //}
                }
            }
        }
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PROTEIN_DETAILS.equals(type)) {
            addProteinDetails(newData.getValue());
        } else if (TableContentType.PROTEIN_SEQUENCE_COVERAGE.equals(type)) {
            addSequenceCoverageData(newData.getValue());
        } else if (TableContentType.PEPTIDE_FIT.equals(type)) {
            addPeptideFitData(newData.getValue());
        } else if (TableContentType.PEPTIDE_DELTA.equals(type)) {
            addPeptideDeltaData(newData.getValue());
        } else if (TableContentType.PEPTIDE_PRECURSOR_MZ.equals(type)) {
            addPeptideMzData(newData.getValue());
        }
    }

    /**
     * Add protein related details
     *
     * @param newData protein detail map
     */
    protected void addProteinDetails(Object newData) {
        // column index for mapped protein accession column
        int mappedAccIndex = getColumnIndex(TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader());
        // column index for protein name
        int identNameIndex = getColumnIndex(TableHeader.PROTEIN_NAME.getHeader());
        // column index for protein status
        int identStatusIndex = getColumnIndex(TableHeader.PROTEIN_STATUS.getHeader());

        // get a map of protein accession to protein details
        Map<String, Protein> proteins = (Map<String, Protein>) newData;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = contents.get(row);
            Object proteinAccession = content.get(mappedAccIndex);
            if (proteinAccession != null) {
                String mappedAccession = ((ProteinAccession) proteinAccession).getMappedAccession();
                if (mappedAccession != null) {
                    Protein protein = proteins.get(mappedAccession);
                    if (protein != null) {
                        AnnotatedProtein annotatedProtein = new AnnotatedProtein(protein);
                        // set protein name
                        content.set(identNameIndex, annotatedProtein.getName());
                        // set protein status
                        content.set(identStatusIndex, annotatedProtein.getStatus().name());
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
    protected void addSequenceCoverageData(Object newData) {
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        // column index for protein sequence coverage
        int coverageIndex = getColumnIndex(TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());

        // map contains sequence coverage
        Map<Comparable, Double> coverageMap = (Map<Comparable, Double>) newData;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = contents.get(row);
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

    /**
     * Whether peptide sequence fit the protein sequence
     *
     * @param newDataValue
     */
    protected void addPeptideFitData(Object newDataValue) {
        // map contains peptide fit
        Map<Tuple<Comparable, Comparable>, Integer> peptideFits = (Map<Tuple<Comparable, Comparable>, Integer>) newDataValue;

        // column index for peptide fit
        int peptideFitIndex = getColumnIndex(TableHeader.PEPTIDE_FIT.getHeader());
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        // column index for peptide id
        int peptideIdIndex = getColumnIndex(TableHeader.PEPTIDE_ID.getHeader());

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = contents.get(row);
            Comparable identId = (Comparable) content.get(identIdIndex);
            Comparable peptideId = (Comparable) content.get(peptideIdIndex);
            Integer peptideFit = peptideFits.get(new Tuple<Comparable, Comparable>(identId, peptideId));
            if (peptideFit != null) {
                // set protein name
                content.set(peptideFitIndex, peptideFit);
                // notify a row change
                fireTableCellUpdated(row, peptideFitIndex);
            }
        }
    }

    /**
     * The Delta mass between the Peptide MZ and the Spectrum MZ
     *
     * @param newDataValue
     */
    protected void addPeptideDeltaData(Object newDataValue) {

        Map<Tuple<Comparable, Comparable>, Double> peptideFits = (Map<Tuple<Comparable, Comparable>, Double>) newDataValue;

        // column index for peptide fit
        int peptideFitIndex = getColumnIndex(TableHeader.DELTA_MASS_COLUMN.getHeader());
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        // column index for peptide id
        int peptideIdIndex = getColumnIndex(TableHeader.PEPTIDE_ID.getHeader());

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = contents.get(row);
            Comparable identId = (Comparable) content.get(identIdIndex);
            Comparable peptideId = (Comparable) content.get(peptideIdIndex);
            Double peptideFit = peptideFits.get(new Tuple<Comparable, Comparable>(identId, peptideId));
            if (peptideFit != null) {
                // set protein name
                content.set(peptideFitIndex, peptideFit);
                // notify a row change
                fireTableCellUpdated(row, peptideFitIndex);
            }
        }
    }

    /**
     * The Precursor Mz
     *
     * @param newDataValue
     */
    protected void addPeptideMzData(Object newDataValue) {
        // map contains peptide fit
        Map<Tuple<Comparable, Comparable>, Double> peptideFits = (Map<Tuple<Comparable, Comparable>, Double>) newDataValue;

        // column index for peptide fit
        int peptideFitIndex = getColumnIndex(TableHeader.PRECURSOR_MZ_COLUMN.getHeader());
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        // column index for peptide id
        int peptideIdIndex = getColumnIndex(TableHeader.PEPTIDE_ID.getHeader());

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = contents.get(row);
            Comparable identId = (Comparable) content.get(identIdIndex);
            Comparable peptideId = (Comparable) content.get(peptideIdIndex);
            Double peptideFit = peptideFits.get(new Tuple<Comparable, Comparable>(identId, peptideId));
            if (peptideFit != null) {
                // set protein name
                content.set(peptideFitIndex, peptideFit);
                // notify a row change
                fireTableCellUpdated(row, peptideFitIndex);
            }
        }
    }
}
