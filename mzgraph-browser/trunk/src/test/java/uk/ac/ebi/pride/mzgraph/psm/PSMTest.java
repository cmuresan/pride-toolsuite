package uk.ac.ebi.pride.mzgraph.psm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzDataControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzXmlControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.data.core.CvParam;
import uk.ac.ebi.pride.data.core.FragmentIon;
import uk.ac.ebi.pride.data.core.Protein;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.mol.NeutralLoss;
import uk.ac.ebi.pride.mol.PTModification;
import uk.ac.ebi.pride.mol.Peptide;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;
import uk.ac.ebi.pride.mol.ion.FragmentIonUtilities;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

/**
 * Creator: Qingwei-XU
 * Date: 07/11/12
 */

public class PSMTest {
    private Peptide toPeptide(uk.ac.ebi.pride.data.core.Peptide oldPeptide) {
        String sequence = oldPeptide.getSequence();
        List<uk.ac.ebi.pride.data.core.Modification> oldModifications = oldPeptide.getModifications();

        Peptide newPeptide = new Peptide(sequence);
        PTModification newModification;

        String name;
        String type = null;
        String label;
        List<Double> monoMassDeltas;
        List<Double> avgMassDeltas;
        int position;
        for (uk.ac.ebi.pride.data.core.Modification oldModification : oldModifications) {
            name = oldModification.getName();
            label = "111";
            monoMassDeltas = oldModification.getMonoisotopicMassDelta();
            avgMassDeltas = oldModification.getAvgMassDelta();
            newModification = new PTModification(name, type, label, monoMassDeltas, avgMassDeltas);

            /**
             * old modification position from [0..length], 0 means the position locate in c-terminal.
             * the new modification from [0..length-1], 0 means the first amino acid of peptide.
             * The modification worked in c-terminal or first amino acid, the theoretical mass are same.
             */
            position = oldModification.getLocation() - 1;
            if (position == -1) {
                position = 0;
            }

            newPeptide.addModification(position, newModification);
        }

        return newPeptide;
    }

    private FragmentIonType getIonType(FragmentIon ion) {
        return FragmentIonUtilities.getFragmentIonType(ion.getIonType());
    }

    private IonAnnotation getOverlapIonAnnotation(FragmentIon ion, List<IonAnnotation> ionAnnotations) {
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

    private List<IonAnnotation> convertToIonAnnotations(List<FragmentIon> ions) {
        List<IonAnnotation> ionAnnotations = new ArrayList<IonAnnotation>();
        if (ions != null) {
            for (FragmentIon ion : ions) {
                // get the fragment ion type
                FragmentIonType ionType = getIonType(ion);
                // get the fragment loss
                NeutralLoss fragLoss = FragmentIonUtilities.getFragmentIonNeutralLoss(ion.getIonType());
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

    private List<IonAnnotation> getAutoAnnotationList(uk.ac.ebi.pride.data.core.Peptide peptide, Spectrum spectrum) {
        Peptide newPeptide = toPeptide(peptide);
        int charge = 0;
        try {
            List<CvParam> params = spectrum.getPrecursors().get(0).getSelectedIons().get(0).getCvParams();
            for (CvParam param : params) {
                if (param.getName().equals("ChargeState")) {
                    charge = Integer.parseInt(param.getValue());
                    break;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            charge = charge == 0 ? 2 : charge;
            if (charge > 2) {
                charge = 2;
            }
        }
        double[] mzArray = spectrum.getMzBinaryDataArray().getDoubleArray();
        double[] intensityArray = spectrum.getIntensityBinaryDataArray().getDoubleArray();

        PrecursorIon precursorIon = new DefaultPrecursorIon(newPeptide, charge);
        ExperimentalFragmentedIonsTableModel tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, ProductIonPair.B_Y);
        tableModel.setPeaks(mzArray, intensityArray);
        return tableModel.getAutoAnnotations();
    }

    private List<IonAnnotation> getManualAnnotationList(uk.ac.ebi.pride.data.core.Peptide peptide, Spectrum spectrum) {
        List<FragmentIon> ions = peptide.getFragmentation();
        List<IonAnnotation> annotationList = convertToIonAnnotations(ions);

        List<IonAnnotation> newAnnotationList = new ArrayList<IonAnnotation>();

        for (Iterator<IonAnnotation> iterator = annotationList.iterator(); iterator.hasNext(); ) {
            IonAnnotation next =  iterator.next();
            FragmentIonType type = next.getAnnotationInfo().getItem(0).getType();
            if (type.equals(FragmentIonType.B_ION) || type.equals(FragmentIonType.Y_ION)) {
                newAnnotationList.add(next);
            }
        }

        return newAnnotationList;

    }

    /**
     * Calculate the overlap (percent value) between auto annotation list and manual annotation list.
     * There are three parts in overlap output:
     * <P>A means Auto annotations collection, M means Manual annotations collection.</P>
     * <ol>
     *     <li>auto annotation list size</li>
     *     <li>overlap in auto: (A and M) / A</li>
     *     <li>manual annotation list size</li>
     *     <li>overlap in manual: (A and M) / M</li>
     *     <li>the overlap factor: (A and M) / (A or M)</li>
     * </ol>
     *
     */
    private String overlap(List<IonAnnotation> autoList, List<IonAnnotation> manualList) {
        Set<IonAnnotation> union = new HashSet<IonAnnotation>();
        Set<IonAnnotation> intersection = new HashSet<IonAnnotation>();
        union.addAll(manualList);

        for (IonAnnotation autoItem : autoList) {
            if (! manualList.contains(autoItem)) {
                union.add(autoItem);
            } else {
                intersection.add(autoItem);
            }
        }

        int interSize = intersection.size();
        int unionSize = union.size();
        int autoSize = autoList.size();
        int manualSize = manualList.size();

        double overlap = interSize * 100.0d / unionSize;
        double overlap_a = autoSize == 0 ? autoSize : interSize * 100.0d / autoSize;
        double overlap_m = manualSize == 0 ? manualSize : interSize * 100.0d / manualSize;

        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);

        return autoSize + "\t" + formatter.format(overlap_a) + "\t" +
               manualSize + "\t" + formatter.format(overlap_m) +"\t" +
               formatter.format(overlap);
    }

    private String testSpectrum(uk.ac.ebi.pride.data.core.Peptide peptide, Spectrum spectrum) {


        List<IonAnnotation> autoList = getAutoAnnotationList(peptide, spectrum);
        List<IonAnnotation> manualList = getManualAnnotationList(peptide, spectrum);

        return overlap(autoList, manualList);
    }

    public static void main(String[] args) throws Exception {
        String ret = "\r\n";

        long start = new Date().getTime();

        File inputFile = new File(args[0]);
        File outFile = new File(args[1] + "-" + start + ".csv");

        PrideXmlControllerImpl controller = new PrideXmlControllerImpl(inputFile);

        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        String title = "Auto\tOverlap_A(%)\tManual\tOverlap_M(%)\tOverlap(%)";
        writer.write(title + ret);

        Protein protein;
        Spectrum spectrum;
        uk.ac.ebi.pride.data.core.Peptide peptide;
        PSMTest test = new PSMTest();
        int count = 0;
        for (int i = 0; i < controller.getProteinIds().size(); i++) {
            protein = controller.getProteinById(i);
            for (java.util.Iterator<uk.ac.ebi.pride.data.core.Peptide> iterator = protein.getPeptides().iterator(); iterator.hasNext(); ) {
                peptide = iterator.next();
                spectrum  =  peptide.getSpectrum();
                count++;
                writer.write(test.testSpectrum(peptide, spectrum) + ret);
            }
        }

        long end = new Date().getTime();
        long elapse = end - start;


        writer.close();
        controller.close();

        System.out.println("total work: " + elapse + "(ms)");
        System.out.println("total count:" + count);
    }

}
