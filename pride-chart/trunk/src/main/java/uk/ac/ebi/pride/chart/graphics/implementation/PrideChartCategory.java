package uk.ac.ebi.pride.chart.graphics.implementation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * <p>Enumeration of the different PRIDE-CHART classes</p>
 *
 * @author Antonio Fabregat
 * Date: 12-oct-2010
 * Time: 16:51:33
 */
public enum PrideChartCategory {
    GENERAL,
    PROTEIN,
    PEPTIDE,
    SPECTRUM,
    CHROMATOGRAM;

    /**
     * Contains the correspondences between pride chart identifiers and the set of categories
     */
    private static final HashMap<Integer, HashSet<PrideChartCategory>> correspondences = new HashMap<Integer, HashSet<PrideChartCategory>>();

    /**
     * Initialization of the correspondences
     */
    static {
        correspondences.put(
                PrideChartFactory.FrequencyPrecursorIonCharge,
                new HashSet<PrideChartCategory>(Arrays.asList(
                        GENERAL, //SPECTRUM,
                        //ToDo: Next Doesn't make sense -> needed a change in the cathegory approach for not showing
                        //Todo: the Frequency Precursor Ion Charge in the mzML files
                        PEPTIDE, PROTEIN
                )));

        correspondences.put(
                PrideChartFactory.IntensityHistogram,
                new HashSet<PrideChartCategory>(Arrays.asList(
                        GENERAL, SPECTRUM
                )));

        correspondences.put(
                PrideChartFactory.MassDelta,
                new HashSet<PrideChartCategory>(Arrays.asList(
                        GENERAL, PEPTIDE
                )));

        correspondences.put(
                PrideChartFactory.MZHistogram,
                new HashSet<PrideChartCategory>(Arrays.asList(
                        GENERAL, SPECTRUM
                )));

        correspondences.put(
                PrideChartFactory.PeaksHistogram,
                new HashSet<PrideChartCategory>(Arrays.asList(
                        GENERAL, SPECTRUM
                )));

        correspondences.put(
                PrideChartFactory.PrecursorMassesDistribution,
                new HashSet<PrideChartCategory>(Arrays.asList(
                        GENERAL, SPECTRUM
                )));

        correspondences.put(
                PrideChartFactory.ProteinsPeptides,
                new HashSet<PrideChartCategory>(Arrays.asList(
                        GENERAL, PEPTIDE, PROTEIN
                )));

        correspondences.put(
                PrideChartFactory.MissedCleavages,
                new HashSet<PrideChartCategory>(Arrays.asList(
                        GENERAL, PEPTIDE, PROTEIN
                )));

        correspondences.put(
                PrideChartFactory.MascotScore,
                new HashSet<PrideChartCategory>(Arrays.asList(
                        GENERAL, PEPTIDE, PROTEIN
                )));

    }

    /**
     * Returns the corresponding categories for a pride chart identifier (null if identifier does not match with any PRIDE-Chart)
     *
     * @param prideChartIdentifier the pride chart identifier
     * @return the corresponding categories for a pride chart identifier
     */
    public static HashSet getCategories(int prideChartIdentifier) {
        HashSet categories = null;
        if (correspondences.containsKey(prideChartIdentifier)) {
            categories = correspondences.get(prideChartIdentifier);
        } else {
            categories = new HashSet();
        }
        return categories;
    }

    /**
     * Returns true if chart corresponding to the identifier is of the expected category
     *
     * @param identifier the identifier associated to the PrideChart instance
     * @param category   the expected category
     * @return true if chart corresponding to the identifier is of the expected category
     */
    public static boolean isOfCategory(int identifier, PrideChartCategory category) {
        return getCategories(identifier).contains(category);
    }
}