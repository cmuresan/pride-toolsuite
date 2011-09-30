package uk.ac.ebi.pride.gui.component.table;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.PeptideScore;
import uk.ac.ebi.pride.mol.MoleculeUtilities;
import uk.ac.ebi.pride.util.NumberUtilities;
import uk.ac.ebi.pride.util.ProteinAccessionResolver;

import java.util.*;

/**
 * <code>TableRowDataRetriever </code> provides methods for retrieving row data for tables.
 * <p/>
 * User: rwang
 * Date: 13-Oct-2010
 * Time: 16:56:47
 */
public class TableRowDataRetriever {

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

        // Original Protein Accession
        String protAcc = controller.getProteinAccession(identId);
        String protAccVersion = controller.getProteinAccessionVersion(identId);
        String database = controller.getSearchDatabase(identId);
        content.add(protAcc);

        // Mapped Protein Accession
        content.add(ProteinAccessionResolver.resolve(protAcc, protAccVersion, database));

        // Protein name
        content.add(null);

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
            content.add(deltaMass == null ? null : NumberUtilities.scaleDouble(deltaMass, 4));

            // precursor m/z
            content.add(mz == -1 ? null : NumberUtilities.scaleDouble(mz, 4));
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
        int start = controller.getPeptideSequenceStart(identId, peptideId);
        content.add(start == -1 ? null : start);

        // End
        int end = controller.getPeptideSequenceEnd(identId, peptideId);
        content.add(end == -1 ? null : end);

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
    public static List<Object> getIdentificationTableRow(DataAccessController controller,
                                                         Comparable identId) throws DataAccessException {
        List<Object> content = new ArrayList<Object>();

        // Original Protein Accession
        String protAcc = controller.getProteinAccession(identId);
        String protAccVersion = controller.getProteinAccessionVersion(identId);
        String database = controller.getSearchDatabase(identId);
        content.add(protAcc);

        // Mapped Protein Accession
        String resolvedProtAcc = ProteinAccessionResolver.resolve(protAcc, protAccVersion, database);
        content.add(resolvedProtAcc);

        // protein name
        content.add(null);

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
}
