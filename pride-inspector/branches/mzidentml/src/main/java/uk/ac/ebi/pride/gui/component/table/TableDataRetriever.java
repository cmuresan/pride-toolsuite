package uk.ac.ebi.pride.gui.component.table;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.utils.QuantCvTermReference;
import uk.ac.ebi.pride.gui.PrideInspectorCacheManager;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.gui.component.sequence.PeptideFitState;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableRow;
import uk.ac.ebi.pride.gui.utils.Constants;
import uk.ac.ebi.pride.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.mol.IsoelectricPointUtils;
import uk.ac.ebi.pride.mol.MoleculeUtilities;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;
import uk.ac.ebi.pride.tools.utils.AccessionResolver;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.util.*;

/**
 * <code>TableDataRetriever </code> provides methods for retrieving row data for tables.
 * <p/>
 * User: rwang
 * Date: 13-Oct-2010
 * Time: 16:56:47
 */
public class TableDataRetriever {

    /**
     * Get a row of data for peptide table.
     *
     * @param controller data access controller
     * @param identId    identification id
     * @param peptideId  peptide id
     * @return List<Object> row data
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          data access exception
     */
    public static PeptideTableRow getPeptideTableRow(DataAccessController controller,
                                                  Comparable identId,
                                                  Comparable peptideId) throws DataAccessException {
        PeptideTableRow peptideTableRow = new PeptideTableRow();

        // peptide sequence with modifications
        List<Modification> mods = new ArrayList<Modification>(controller.getPTMs(identId, peptideId));
        String sequence = controller.getPeptideSequence(identId, peptideId);
        peptideTableRow.setSequence(new PeptideSequence(null, null, sequence, mods, null));

        // start and end position
        int start = controller.getPeptideSequenceStart(identId, peptideId);
        int end = controller.getPeptideSequenceEnd(identId, peptideId);

        // Protein Accession
        String protAcc = controller.getProteinAccession(identId);
        String protAccVersion = controller.getProteinAccessionVersion(identId);
        String database = (controller.getSearchDatabase(identId).getName() == null) ? "" : controller.getSearchDatabase(identId).getName();
        AccessionResolver resolver = new AccessionResolver(protAcc, protAccVersion, database, true);
        String mappedProtAcc = resolver.isValidAccession() ? resolver.getAccession() : null;
        peptideTableRow.setProteinAccession(new ProteinAccession(protAcc, mappedProtAcc));

        // get protein details
        Protein protein = PrideInspectorCacheManager.getInstance().getProteinDetails(mappedProtAcc);
        if (protein != null) {
            protein = new AnnotatedProtein(protein);
        }

        // Protein name
        peptideTableRow.setProteinName(protein == null ? null : protein.getName());

        // protein status
        peptideTableRow.setProteinAccessionStatus(protein == null ? null : protein.getStatus().name());

        // sequence coverage
        Double coverage = PrideInspectorCacheManager.getInstance().getSequenceCoverage(controller.getUid(), identId);
        peptideTableRow.setSequenceCoverage(coverage);

        // peptide present
        int peptideFitState;
        if (protein == null || protein.getSequenceString() == null || "".equals(protein.getSequenceString())) {
            peptideFitState = PeptideFitState.UNKNOWN;
        } else {
            if (protein.hasSubSequenceString(sequence, start, end)) {
                peptideFitState = PeptideFitState.STRICT_FIT;
            } else if (protein.hasSubSequenceString(sequence)) {
                peptideFitState = PeptideFitState.FIT;
            } else {
                peptideFitState = PeptideFitState.NOT_FIT;
            }
        }
        peptideTableRow.setPeptideFitState(peptideFitState);

        // ranking
        int rank = controller.getPeptideRank(identId, peptideId);
        peptideTableRow.setRanking(rank);

        // precursor charge
        Integer charge = controller.getPeptidePrecursorCharge(identId, peptideId);
        Comparable specId = controller.getPeptideSpectrumId(identId, peptideId);
        if (charge == null && specId != null) {
            charge = controller.getSpectrumPrecursorCharge(specId);
            if (charge == null || charge == 0) {
                charge = null;
            }
        }
        peptideTableRow.setPrecursorCharge(charge);

        if (specId != null) {
            double mz = controller.getSpectrumPrecursorMz(specId);
            List<Double> ptmMasses = new ArrayList<Double>();
            for (Modification mod : mods) {
                List<Double> monoMasses = mod.getMonoisotopicMassDelta();
                if (monoMasses != null && !monoMasses.isEmpty()) {
                    ptmMasses.add(monoMasses.get(0));
                }
            }
            Double deltaMass = MoleculeUtilities.calculateDeltaMz(sequence, mz, charge, ptmMasses);
            peptideTableRow.setDeltaMz(deltaMass == null ? null : NumberUtilities.scaleDouble(deltaMass, 2));

            peptideTableRow.setPrecursorMz(mz == -1 ? null : NumberUtilities.scaleDouble(mz, 2));
        } else {
            peptideTableRow.setDeltaMz(null);
            peptideTableRow.setPrecursorMz(null);
        }

        // Number of fragment ions
        peptideTableRow.setNumberOfFragmentIons(controller.getNumberOfFragmentIons(identId, peptideId));

        // peptide scores
        addPeptideScores(peptideTableRow, controller, identId, peptideId);

        // Start
        peptideTableRow.setSequenceStartPosition(start == -1 ? null : start);

        // End
        peptideTableRow.setSequenceEndPosition(end == -1 ? null : end);

        // Spectrum reference
        peptideTableRow.setSpectrumId(specId);

        // identification id
        peptideTableRow.setProteinId(identId);

        // peptide index
        peptideTableRow.setPeptideId(peptideId);

        return peptideTableRow;
    }

    private static void addPeptideScores(PeptideTableRow peptideTableRow, DataAccessController controller,
                                         Comparable identId, Comparable peptideId) {
        Score score = controller.getPeptideScore(identId, peptideId);
        Collection<CvTermReference> availablePeptideLevelScores = controller.getAvailablePeptideLevelScores();
        if (score != null) {
            for (CvTermReference availablePeptideLevelScore : availablePeptideLevelScores) {
                List<Number> values = score.getScores(availablePeptideLevelScore);
                if (!values.isEmpty()) {
                    // take the first by default
                    //content.add(values.get(0));
                    peptideTableRow.addScore(NumberUtilities.scaleDouble(values.get(0).doubleValue(), 4));

                } else {
                    peptideTableRow.addScore(null);
                }
            }
        } else {
            for (CvTermReference availablePeptideLevelScore : availablePeptideLevelScores) {
                peptideTableRow.addScore(null);
            }
        }
    }

    /**
     * Retrieve a row of data for identification table.
     *
     * @param controller data access controller
     * @param identId    identification id
     * @return List<Object> row data
     * @throws DataAccessException data access exception
     */
    public static List<Object> getProteinTableRow(DataAccessController controller,
                                                  Comparable identId) throws DataAccessException {
        List<Object> content = new ArrayList<Object>();

        // Original Protein Accession
        String protAcc = controller.getProteinAccession(identId);
        String protAccVersion = controller.getProteinAccessionVersion(identId);
        String database = (controller.getSearchDatabase(identId).getName() == null) ? "" : controller.getSearchDatabase(identId).getName();
        AccessionResolver resolver = new AccessionResolver(protAcc, protAccVersion, database, true);
        String mappedProtAcc = resolver.isValidAccession() ? resolver.getAccession() : null;
        content.add(new ProteinAccession(protAcc, mappedProtAcc));

        // get protein details
        Protein protein = PrideInspectorCacheManager.getInstance().getProteinDetails(mappedProtAcc);
        if (protein != null) {
            protein = new AnnotatedProtein(protein);
        }

        // Protein name
        content.add(protein == null ? null : protein.getName());

        // protein status
        content.add(protein == null ? null : protein.getStatus().name());

        // sequence coverage
        Double coverage = PrideInspectorCacheManager.getInstance().getSequenceCoverage(controller.getUid(), identId);
        content.add(coverage);

        // isoelectric points
        if (protein != null) {
            String sequence = protein.getSequenceString();
            if (sequence != null) {
                content.add(IsoelectricPointUtils.calculate(protein.getSequenceString()));
            } else {
                content.add(null);
            }
        } else {
            content.add(null);
        }

        // Threshold
        double threshold = controller.getProteinThreshold(identId);
        content.add(threshold == -1 ? null : threshold);

        // number of peptides
        content.add(controller.getNumberOfPeptides(identId));

        // unique peptides
        content.add(controller.getNumberOfUniquePeptides(identId));

        // number of PTMs
        content.add(controller.getNumberOfPTMs(identId));

        // unique id for identification
        content.add(identId);

        // protein scores
        addProteinScores(content, controller, identId);

        // additional details is always null
        content.add(identId);

        return content;
    }

    private static void addProteinScores(List<Object> content, DataAccessController controller, Comparable identId) {
        Score score = controller.getProteinScores(identId);
        Collection<CvTermReference> availablePeptideLevelScores = controller.getAvailablePeptideLevelScores();
        if (score != null) {
            for (CvTermReference availablePeptideLevelScore : availablePeptideLevelScores) {
                List<Number> values = score.getScores(availablePeptideLevelScore);
                if (!values.isEmpty()) {
                    // take the first by default
                    content.add(values.get(0));
                } else {
                    content.add(null);
                }
            }
        } else {
            for (CvTermReference availablePeptideLevelScore : availablePeptideLevelScores) {
                content.add(null);
            }
        }
    }

    /**
     * Get the headers for the identification quantitative table
     *
     * @param controller     data access controller
     * @param refSampleIndex given reference sub sample index
     * @return List<Object>    a list of headers
     * @throws DataAccessException data access exception
     */
    public static List<Object> getProteinQuantTableHeaders(DataAccessController controller, int refSampleIndex) throws DataAccessException {
        return getQuantTableHeaders(controller, refSampleIndex, true);
    }

    /**
     * Get the headers for the peptide quantitative table
     *
     * @param controller     data access controller
     * @param refSampleIndex reference sub sample index
     * @return List<Object> peptide quantitative table headers
     * @throws DataAccessException data access exception
     */
    public static List<Object> getPeptideQuantTableHeaders(DataAccessController controller, int refSampleIndex) throws DataAccessException {
        return getQuantTableHeaders(controller, refSampleIndex, false);

    }

    /**
     * Get table header for quantitative data
     *
     * @param controller     data access controller
     * @param refSampleIndex reference sub sample index
     * @param isProteinIdent whether it is protein identification or peptide identification
     * @return List<Object>    a list of quantitative table headers
     * @throws DataAccessException data access exception
     */
    private static List<Object> getQuantTableHeaders(DataAccessController controller, int refSampleIndex, boolean isProteinIdent) throws DataAccessException {
        List<Object> headers = new ArrayList<Object>();

        // label free methods
        if (controller.hasLabelFreeQuantMethods()) {
            Collection<QuantCvTermReference> methods = isProteinIdent ? controller.getProteinLabelFreeQuantMethods() : controller.getPeptideLabelFreeQuantMethods();
            headers.addAll(getLabelFreeMethodHeaders(methods));
        }

        // isotope labelling methods
        if (controller.hasIsotopeLabellingQuantMethods()) {
            Collection<QuantCvTermReference> methods = isProteinIdent ? controller.getProteinIsotopeLabellingQuantMethods() : controller.getPeptideIsotopeLabellingQuantMethods();
            headers.addAll(getIsotopeLabellingMethodHeaders(methods, controller, refSampleIndex, isProteinIdent));
        }

        return headers;
    }

    /**
     * Create isotope labelling method headers
     *
     * @param methods        isotope labelling methods
     * @param controller     data access controller
     * @param refSampleIndex reference sub sample index
     * @param isProteinIdent whether is protein identification or peptide identification
     * @return List<Object>    a list of headers
     * @throws DataAccessException data access exception
     */
    private static List<Object> getIsotopeLabellingMethodHeaders(Collection<QuantCvTermReference> methods,
                                                                 DataAccessController controller,
                                                                 int refSampleIndex,
                                                                 boolean isProteinIdent) throws DataAccessException {
        List<Object> headers = new ArrayList<Object>();

        if (methods.size() > 0) {
            QuantitativeSample sample = controller.getQuantSample();
            // total intensities
            boolean hasTotalIntensities = isProteinIdent ? controller.hasProteinTotalIntensities() : controller.hasPeptideTotalIntensities();
            if (hasTotalIntensities) {
                headers.addAll(getTotalIntensityHeaders(sample));
            }

            int existingRefSampleIndex = controller.getReferenceSubSampleIndex();

            if (refSampleIndex < 1 || refSampleIndex == existingRefSampleIndex) {
                // show the original ratios
                if (existingRefSampleIndex >= 1) {
                    // the original quant data has a reference sample already
                    headers.addAll(getReagentRatioHeaders(sample, existingRefSampleIndex));
                }
            } else {
                // show the newly calculated ratios
                headers.addAll(getReagentRatioHeaders(sample, refSampleIndex));
            }

        }

        return headers;
    }


    /**
     * Create a list of label free method headers
     *
     * @param methods label free methods
     * @return List<Object>    label free method headers
     */
    private static List<Object> getLabelFreeMethodHeaders(Collection<QuantCvTermReference> methods) {
        List<Object> headers = new ArrayList<Object>();

        for (QuantCvTermReference method : methods) {
            headers.add(method.getName());
        }

        return headers;
    }

    /**
     * Create a list of headers for intensities
     *
     * @param sample qauntitative sample
     * @return List<String>    total intensity headers
     */
    private static List<Object> getTotalIntensityHeaders(QuantitativeSample sample) {
        List<Object> headers = new ArrayList<Object>();

        for (int i = 1; i <= QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
            CvParam reagent = sample.getReagent(i);
            if (reagent != null) {
                headers.add(QuantCvTermReference.getReagentShortLabel(reagent));
            }
        }

        return headers;
    }

    /**
     * Create a list of headers for reagents accorrding to the given reference sample
     *
     * @param sample         qauntitative sample
     * @param refSampleIndex reference sub sample index
     * @return List<Object>    a list of headers
     */
    private static List<Object> getReagentRatioHeaders(QuantitativeSample sample, int refSampleIndex) {
        List<Object> headers = new ArrayList<Object>();

        // get reference reagent
        CvParam referenceReagent = sample.getReagent(refSampleIndex);
        // get short label for the reagent
        String shortenedReferenceReagent = QuantCvTermReference.getReagentShortLabel(referenceReagent);
        for (int i = 1; i < QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
            if (refSampleIndex != i) {
                CvParam reagent = sample.getReagent(i);
                if (reagent != null) {
                    headers.add(QuantCvTermReference.getReagentShortLabel(reagent) + Constants.QUANTIFICATION_RATIO_CHAR + shortenedReferenceReagent);
                }
            }
        }

        return headers;
    }

    /**
     * Retrieve a row for identification quantitative table
     *
     * @param controller              data access controller
     * @param identId                 identification id
     * @param referenceSubSampleIndex reference sub sample index
     * @return List<Object> a list of results
     * @throws DataAccessException data access exception
     */
    public static List<Object> getProteinQuantTableRow(DataAccessController controller,
                                                       Comparable identId,
                                                       int referenceSubSampleIndex) throws DataAccessException {
        Quantification quant = controller.getProteinQuantData(identId);
        return getQuantTableRow(controller, quant, referenceSubSampleIndex, true);
    }


    /**
     * Retrieve a row for peptide quantitative table
     *
     * @param controller              data access controller
     * @param identId                 identification id
     * @param peptideId               peptide id
     * @param referenceSubSampleIndex reference sub sample index
     * @return List<Object>    a list of results
     * @throws DataAccessException data access exception
     */
    public static List<Object> getPeptideQuantTableRow(DataAccessController controller,
                                                       Comparable identId,
                                                       Comparable peptideId,
                                                       int referenceSubSampleIndex) throws DataAccessException {
        Quantification quant = controller.getPeptideQuantData(identId, peptideId);
        return getQuantTableRow(controller, quant, referenceSubSampleIndex, false);
    }

    /**
     * Get table header for quantitative data
     *
     * @param controller     data access controller
     * @param quant          quantitative data
     * @param refSampleIndex reference sub sample index
     * @param isProteinIdent whether it is protein identification or peptide identification
     * @return List<String>    a list of quantitative table headers
     * @throws DataAccessException data access exception
     */
    private static List<Object> getQuantTableRow(DataAccessController controller, Quantification quant, int refSampleIndex, boolean isProteinIdent) throws DataAccessException {
        List<Object> contents = new ArrayList<Object>();

        // label free methods
        if (controller.hasLabelFreeQuantMethods()) {
            Collection<QuantCvTermReference> methods = isProteinIdent ? controller.getProteinLabelFreeQuantMethods() : controller.getPeptideLabelFreeQuantMethods();
            contents.addAll(getLabelFreeQuantData(methods, quant));
        }

        // isotope labelling methods
        if (controller.hasIsotopeLabellingQuantMethods()) {
            Collection<QuantCvTermReference> methods = isProteinIdent ? controller.getProteinIsotopeLabellingQuantMethods() : controller.getPeptideIsotopeLabellingQuantMethods();
            contents.addAll(getIsotopeLabellingQuantData(methods, controller, quant, refSampleIndex, isProteinIdent));
        }

        return contents;
    }

    /**
     * Get label free quantitative data
     *
     * @param methods label free methods
     * @param quant   quantitative object
     * @return List<Double>    a list of label free results
     */
    private static List<Double> getLabelFreeQuantData(Collection<QuantCvTermReference> methods, Quantification quant) {
        return quant.getLabelFreeResults(methods);
    }

    /**
     * Get isotope labeling quantitative data
     *
     * @param methods        isotople labelling methods
     * @param controller     data access controller
     * @param quant          quantitative object
     * @param refSampleIndex reference sub sample index
     * @param isProteinIdent whether is protein identification or peptide identification
     * @return List<Object>    a list of results
     * @throws DataAccessException data access exception
     */
    private static List<Object> getIsotopeLabellingQuantData(Collection<QuantCvTermReference> methods, DataAccessController controller,
                                                             Quantification quant, int refSampleIndex, boolean isProteinIdent) throws DataAccessException {

        List<Object> contents = new ArrayList<Object>();

        if (methods.size() > 0) {
            QuantitativeSample sample = controller.getQuantSample();
            // total intensities
            boolean hasTotalIntensities = isProteinIdent ? controller.hasProteinTotalIntensities() : controller.hasPeptideTotalIntensities();
            if (hasTotalIntensities) {
                contents.addAll(getTotalIntensityQuantData(sample, quant));
            }

            int existingRefSampleIndex = controller.getReferenceSubSampleIndex();

            if (refSampleIndex < 1 || refSampleIndex == existingRefSampleIndex) {
                // show the original ratios
                if (existingRefSampleIndex >= 1) {
                    // the original quant data has a reference sample already
                    contents.addAll(getReagentRatioQuantData(sample, quant, existingRefSampleIndex));
                }
            } else {
                // show the newly calculated ratios
                contents.addAll(getReagentRatioQuantData(sample, quant, refSampleIndex));
            }

        }

        return contents;
    }

    /**
     * Get total intensities
     *
     * @param sample quantitative sample
     * @param quant  quantitative data
     * @return List<Object>    a list of total intensities
     */
    private static List<Object> getTotalIntensityQuantData(QuantitativeSample sample, Quantification quant) {
        List<Object> contents = new ArrayList<Object>();

        for (int i = 1; i <= QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
            CvParam reagent = sample.getReagent(i);
            if (reagent != null) {
                contents.add(quant.getIsotopeLabellingResult(i));
            }
        }

        return contents;
    }


    /**
     * Get reagent quantitative data
     *
     * @param sample         quantitative sample
     * @param quant          quantitative data
     * @param refSampleIndex reference sub sample index
     * @return List<Object>    a list of reagent ratio data
     */
    private static List<Object> getReagentRatioQuantData(QuantitativeSample sample,
                                                         Quantification quant,
                                                         int refSampleIndex) {
        List<Object> contents = new ArrayList<Object>();

        // get reference reagent
        Double referenceReagentResult = quant.getIsotopeLabellingResult(refSampleIndex);
        // get short label for the reagent
        for (int i = 1; i < QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
            if (refSampleIndex != i) {
                CvParam reagent = sample.getReagent(i);
                if (reagent != null) {
                    Double reagentResult = quant.getIsotopeLabellingResult(i);
                    if (referenceReagentResult != null && reagentResult != null) {
                        contents.add(reagentResult / referenceReagentResult);
                    } else {
                        contents.add(null);
                    }
                }
            }
        }

        return contents;
    }
}
