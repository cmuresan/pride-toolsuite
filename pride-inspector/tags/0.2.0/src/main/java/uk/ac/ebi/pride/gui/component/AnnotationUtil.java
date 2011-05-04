package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.core.FragmentIon;
import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.utils.CvTermReference;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.mzgraph.chart.data.residue.*;

import java.util.*;

/**
 * AnnotationUtil provides static methods for converting information into mzgraph-browser format.
 * <p/>
 * User: rwang
 * Date: 03-Aug-2010
 * Time: 11:01:14
 */
public class AnnotationUtil {

    public static List<IonAnnotation> convertToIonAnnotations(List<FragmentIon> ions) {
        List<IonAnnotation> ionAnnotations = new ArrayList<IonAnnotation>();
        if (ions != null) {
            for (FragmentIon ion : ions) {
                // get the fragment ion type
                FragmentIonType ionType = getIonType(ion);
                // get the fragment loss
                NeutralLoss fragLoss = ResidueHelper.getNeutralLoss(ion.getIonType());
                // m/z and intensity
                IonAnnotation ionAnnotation = getOverlapIonAnnotation(ion, ionAnnotations);
                IonAnnotationInfo ionInfo;
                if (ionAnnotation == null) {
                    ionInfo = new IonAnnotationInfo();
                    ionAnnotation = new IonAnnotation(ion.getMz(), ion.getIntensity(), ionInfo);
                    ionAnnotations.add(ionAnnotation);
                } else {
                    ionInfo = ionAnnotation.getAnnotationInfo();
                }
                ionInfo.addItem(ion.getCharge(), ionType, ion.getLocation(), fragLoss);
            }
        }
        return ionAnnotations;
    }

    public static IonAnnotation getOverlapIonAnnotation(FragmentIon ion, List<IonAnnotation> ionAnnotations) {
        IonAnnotation result = null;
        double mz = ion.getMz();
        double intensity = ion.getIntensity();

        for (IonAnnotation ionAnnotation : ionAnnotations) {
            if (ionAnnotation.getMz().doubleValue() == mz
                    && ionAnnotation.getIntensity().doubleValue() == intensity) {
                result = ionAnnotation;
            }
        }
        return result;
    }

    /**
     * Convert fragment ion.
     *
     * @param ion fragment ion.
     * @return IonAnnotation    ion annotation.
     */
    public static IonAnnotation getIonAnnotation(FragmentIon ion) {
        // get the fragment ion type
        FragmentIonType ionType = getIonType(ion);

        // get the fragment loss
        NeutralLoss fragLoss = ResidueHelper.getNeutralLoss(ion.getIonType());
        IonAnnotationInfo ionInfo = new IonAnnotationInfo();
        ionInfo.addItem(ion.getCharge(), ionType, ion.getLocation(), fragLoss);
        return new IonAnnotation(ion.getMz(), ion.getIntensity(), ionInfo);
    }

    public static FragmentIonType getIonType(FragmentIon ion) {
        CvTermReference ionCvTerm = CvTermReference.getCvRefByAccession(ion.getIonTypeAccession());
        FragmentIonType ionType = ResidueHelper.getFragmentIonType(ionCvTerm.getAccession());
        if (ionType.equals(FragmentIonType.NON_IDENTIFIED_ION)) {
            // iterate over all the accessions.
            Collection<String> parentAccs = ionCvTerm.getParentAccessions();
            for (String parentAcc : parentAccs) {
                ionType = ResidueHelper.getFragmentIonType(parentAcc);
                if (!ionType.equals(FragmentIonType.NON_IDENTIFIED_ION)) {
                    break;
                }
            }
        }
        return ionType;
    }

    public static Map<Integer, List<uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification>> createModificationMap(List<Modification> mods, int peptideLength) {
        Map<Integer, List<uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification>> modMap
                = new HashMap<Integer, List<uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification>>();
        for (uk.ac.ebi.pride.data.core.Modification mod : mods) {
            int location = mod.getLocation();
            // merge the N-terminus modification to the first amino acid
            location = location == 0 ? 1 : location;
            // merge the C-terminus modification to the last amino acid
            location = location == peptideLength ? location - 1 : location;

            List<uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification> subMods = modMap.get(location);
            if (subMods == null) {
                subMods = new ArrayList<uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification>();
                modMap.put(mod.getLocation(), subMods);
            }
            subMods.add(new uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification(mod.getName(), mod.getModDatabase(),
                    mod.getName(), mod.getMonoMassDeltas(), mod.getAvgMassDeltas()));
        }
        return modMap;
    }

    public static List<uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification> convertModifications(List<Modification> modifications) {
        List<uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification> newMods = new ArrayList<uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification>();
        for (Modification mod : modifications) {
            newMods.add(new uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification(mod.getName(), mod.getModDatabase(),
                    mod.getName(), mod.getMonoMassDeltas(), mod.getAvgMassDeltas()));
        }
        return newMods;
    }

    public static Peptide getPeptideFromString(String peptideStr) {
        Peptide peptide = new Peptide();
        if (peptideStr != null) {
            char[] chars = peptideStr.toCharArray();
            for (char aChar : chars) {
                AminoAcid aminoAcid = ResidueHelper.getAminoAcidByOneLetterCode(aChar);
                if (aminoAcid != null) {
                    peptide.addAminoAcid(aminoAcid);
                }
            }
            if (peptide.getNumberOfAminoAcids() != peptideStr.length()) {
                peptide.removeAllAminoAcids();    
            }
        }
        return peptide;
    }
}
