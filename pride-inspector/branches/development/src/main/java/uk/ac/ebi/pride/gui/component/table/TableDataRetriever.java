package uk.ac.ebi.pride.gui.component.table;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.utils.QuantCvTermReference;
import uk.ac.ebi.pride.gui.PrideInspectorCacheManager;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.gui.component.sequence.PeptideFitState;
import uk.ac.ebi.pride.mol.IsoelectricPointUtils;
import uk.ac.ebi.pride.mol.MoleculeUtilities;
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
    public static List<Object> getPeptideTableRow(DataAccessController controller,
                                                  Comparable identId,
                                                  Comparable peptideId) throws DataAccessException {
        List<Object> content = new ArrayList<Object>();

        // peptide sequence with modifications
        List<Modification> mods = new ArrayList<Modification>(controller.getPTMs(identId, peptideId));
        String sequence = controller.getPeptideSequence(identId, peptideId);
        content.add(new Peptide(null, sequence, 0, 0, mods, null, null));

        // start and end position
        int start = controller.getPeptideSequenceStart(identId, peptideId);
        int end = controller.getPeptideSequenceEnd(identId, peptideId);


        // Original Protein Accession
        String protAcc = controller.getProteinAccession(identId);
        String protAccVersion = controller.getProteinAccessionVersion(identId);
        String database = controller.getSearchDatabase(identId);
        content.add(protAcc);

        // Mapped Protein Accession
        AccessionResolver resolver = new AccessionResolver(protAcc, protAccVersion, database);
        String mappedProtAcc = resolver.isValidAccession() ? resolver.getAccession() : null;
        content.add(mappedProtAcc);

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

        // peptide present
        if (protein == null || protein.getSequenceString() == null || "".equals(protein.getSequenceString())) {
            content.add(PeptideFitState.UNKNOWN);
        } else {
            if (protein.hasSubSequenceString(sequence, start, end)) {
                content.add(PeptideFitState.STRICT_FIT);
            } else if (protein.hasSubSequenceString(sequence)) {
                content.add(PeptideFitState.FIT);
            } else {
                content.add(PeptideFitState.NOT_FIT);
            }
        }

        // precursor charge
        Comparable specId = controller.getPeptideSpectrumId(identId, peptideId);
        if (specId != null) {
            int charge = controller.getPrecursorCharge(specId);
            content.add(charge == 0 ? null : charge);

            // delta mass
            // theoretical mass
            double mz = controller.getPrecursorMz(specId);
            List<Double> ptmMasses = new ArrayList<Double>();
            for (Modification mod : mods) {
                List<Double> monoMasses = mod.getMonoMassDeltas();
                if (monoMasses != null && !monoMasses.isEmpty()) {
                    ptmMasses.add(monoMasses.get(0));
                }
            }
            Double deltaMass = MoleculeUtilities.calculateDeltaMz(sequence, mz, charge, ptmMasses);
            content.add(deltaMass == null ? null : NumberUtilities.scaleDouble(deltaMass, 2));

            // precursor m/z
            content.add(mz == -1 ? null : NumberUtilities.scaleDouble(mz, 2));
        } else {
            content.add(null);
            content.add(null);
            content.add(null);
        }
        // peptide ptms
        content.add(mods.size());

        // Map for grouping PTMs based on location
        Map<Integer, List<Double>> locationMap = new LinkedHashMap<Integer, List<Double>>();
        // Map for count PTMs based on accession
        Map<String, Integer> accessionCntMap = new LinkedHashMap<String, Integer>();

        // Iterate over each modification
        for (Modification mod : mods) {
            // store the location
            int location = mod.getLocation();
            if (location == 0) {
                location = 1;
            } else if (location == (sequence.length() + 1)) {
                location--;
            }

            List<Double> massDiffs = locationMap.get(location);
            if (massDiffs == null) {
                massDiffs = new ArrayList<Double>();
                locationMap.put(location, massDiffs);
                List<Double> md = mod.getMonoMassDeltas();
                if (md != null && !md.isEmpty()) {
                    massDiffs.add(md.get(0));
                }
            }

            // store the accession
            String accession = mod.getAccession();
            Integer cnt = accessionCntMap.get(accession);
            cnt = cnt == null ? 1 : cnt + 1;
            accessionCntMap.put(accession, cnt);
        }

        // Modified Peptide Sequence
        StringBuilder modPeptide = new StringBuilder();
        for (int i = 0; i < sequence.length(); i++) {
            // append the amino acid
            modPeptide.append(sequence.charAt(i));
            // append mass differences if there is any
            List<Double> massDiffs = locationMap.get(i + 1);
            if (massDiffs != null) {
                modPeptide.append("[");
                if (massDiffs.isEmpty()) {
                    modPeptide.append("*");
                } else {
                    for (int j = 0; j < massDiffs.size(); j++) {
                        if (j != 0) {
                            modPeptide.append(",");
                        }
                        modPeptide.append(NumberUtilities.scaleDouble(massDiffs.get(j), 1));
                    }
                }
                modPeptide.append("]");
            }
        }
        content.add(modPeptide.toString());

        // PTM Accessions with count
        StringBuilder ptmCnt = new StringBuilder();
        for (Map.Entry<String, Integer> entry : accessionCntMap.entrySet()) {
            if (ptmCnt.length() != 0) {
                ptmCnt.append(",");
            }
            ptmCnt.append(entry.getKey());
            ptmCnt.append("[");
            ptmCnt.append(entry.getValue());
            ptmCnt.append("]");
        }
        content.add(ptmCnt.toString());


        // Number of fragment ions
        content.add(controller.getNumberOfFragmentIons(identId, peptideId));

        // peptide scores
        PeptideScore score = controller.getPeptideScore(identId, peptideId);
        if (score != null) {
            List<Number> nums = score.getAllPeptideScores();
            if (nums != null && !nums.isEmpty()) {
                for (Number num : nums) {
                    content.add(num == null ? num : num.doubleValue());
                }
            }
        }

        // Sequence length
        content.add(sequence.length());

        // Start
        content.add(start == -1 ? null : start);

        // End
        content.add(end == -1 ? null : end);

        // Theoritical isoelectric point
//        content.add(IsoelectricPointUtils.calculate(sequence));
        content.add(null);

        // Spectrum reference
        content.add(specId);

        // identification id
        content.add(identId);

        // peptide index
        content.add(peptideId);

        return content;
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
        String database = controller.getSearchDatabase(identId);
        content.add(protAcc);

        // Mapped Protein Accession
        AccessionResolver resolver = new AccessionResolver(protAcc, protAccVersion, database);
        String mappedProtAcc = resolver.isValidAccession() ? resolver.getAccession() : null;
        content.add(mappedProtAcc);

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

        // Score
        double score = controller.getIdentificationScore(identId);
        content.add(score == -1 ? null : score);

        // Threshold
        double threshold = controller.getIdentificationThreshold(identId);
        content.add(threshold == -1 ? null : threshold);

        // number of peptides
        content.add(controller.getNumberOfPeptides(identId));

        // unique peptides
        content.add(controller.getNumberOfUniquePeptides(identId));

        // number of PTMs
        content.add(controller.getNumberOfPTMs(identId));

        // unique id for identification
        content.add(identId);

        return content;
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
                    headers.add(QuantCvTermReference.getReagentShortLabel(reagent) + "/" + shortenedReferenceReagent);
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
        Quantitation quant = controller.getProteinQuantData(identId);
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
        Quantitation quant = controller.getPeptideQuantData(identId, peptideId);
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
    private static List<Object> getQuantTableRow(DataAccessController controller, Quantitation quant, int refSampleIndex, boolean isProteinIdent) throws DataAccessException {
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
    private static List<Double> getLabelFreeQuantData(Collection<QuantCvTermReference> methods, Quantitation quant) {
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
                                                             Quantitation quant, int refSampleIndex, boolean isProteinIdent) throws DataAccessException {

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
    private static List<Object> getTotalIntensityQuantData(QuantitativeSample sample, Quantitation quant) {
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
                                                         Quantitation quant,
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
