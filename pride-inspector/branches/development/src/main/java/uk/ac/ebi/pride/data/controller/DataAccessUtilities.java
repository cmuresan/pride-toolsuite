package uk.ac.ebi.pride.data.controller;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * DataAccessUtilities provides methods for getting information out from the core objects.
 * <p/>
 * User: rwang
 * Date: 30-Aug-2010
 * Time: 11:52:45
 */
public class DataAccessUtilities {

    public static List<Integer> getTaxonomy(MetaData metaData) {
        // todo: to be implemented
        return null;
    }

    public static int getNumberOfPeaks(Spectrum spectrum) {
        int numOfPeaks = -1;
        BinaryDataArray mzArr = spectrum.getBinaryDataArrays().get(0);
        if (mzArr != null) {
            numOfPeaks = mzArr.getDoubleArray().length;
        }
        return numOfPeaks;
    }

    public static int getMsLevel(Spectrum spectrum) {
        int msLevel = -1;
        List<Parameter> param = getParamByName(spectrum, "ms level");
        if (!param.isEmpty()) {
            String val = param.get(0).getValue();
            msLevel = Integer.parseInt(val);
        }
        return msLevel;
    }

    public static int getPrecursorCharge(Spectrum spectrum) {
        int charge = 0;
        List<Precursor> precursors = spectrum.getPrecursors();
        if (precursors != null && !precursors.isEmpty()) {
            Double c = getSelectedIonCharge(precursors.get(0), 0);
            if (c != null) {
                charge = c.intValue();
            }
        }
        return charge;
    }

    public static double getPrecursorMz(Spectrum spectrum) {
        double mz = -1;
        List<Precursor> precursors = spectrum.getPrecursors();
        if (precursors != null && !precursors.isEmpty()) {
            Double m = getSelectedIonMz(precursors.get(0), 0);
            if (m != null) {
                mz = m;
            }
        }
        return mz;
    }

    public static double getPrecursorIntensity(Spectrum spectrum) {
        double intent = -1;
        List<Precursor> precursors = spectrum.getPrecursors();
        if (precursors != null && !precursors.isEmpty()) {
            Double it = getSelectedIonIntensity(precursors.get(0), 0);
            if (it != null) {
                intent = it;
            }
        }
        return intent;
    }

    public static double getSumOfIntensity(Spectrum spectrum) {
        double sum = 0;
        BinaryDataArray intentArr = spectrum.getIntensityBinaryDataArray();
        if (intentArr != null) {
            double[] originalIntentArr = intentArr.getDoubleArray();
            for (double intent : originalIntentArr) {
                sum += intent;
            }
        }
        return sum;
    }

    /**
     * Get the ion charge for a selected ion.
     *
     * @param precursor precursor
     * @param index     index of the selected ion.
     * @return Double   selected ion charge.
     */
    public static Double getSelectedIonCharge(Precursor precursor, int index) {
        return getSelectedIonCvParamValue(precursor, index,
                CvTermReference.PSI_ION_SELECTION_CHARGE_STATE, CvTermReference.ION_SELECTION_CHARGE_STATE);
    }

    /**
     * Get the ion m/z value for a selected ion.
     *
     * @param precursor precursor
     * @param index     index of the selected ion.
     * @return Double   selected ion m/z.
     */
    public static Double getSelectedIonMz(Precursor precursor, int index) {
        return getSelectedIonCvParamValue(precursor, index,
                CvTermReference.PSI_ION_SELECTION_MZ, CvTermReference.ION_SELECTION_MZ);
    }


    /**
     * Get the ion intensity value for a selected ion.
     *
     * @param precursor precursor
     * @param index     index of the selected ion.
     * @return Double   selected ion intensity.
     */
    public static Double getSelectedIonIntensity(Precursor precursor, int index) {
        return getSelectedIonCvParamValue(precursor, index,
                CvTermReference.PSI_ION_SELECTION_INTENSITY, CvTermReference.ION_SELECTION_INTENSITY);
    }

    /**
     * Get value of selected ion cv param.
     *
     * @param precursor precursor
     * @param index     index of the selecte ion.
     * @param refs      a list of possible cv terms to search for.
     * @return Double   value of the cv parameter.
     */
    private static Double getSelectedIonCvParamValue(Precursor precursor, int index, CvTermReference... refs) {
        Double value = null;
        List<ParamGroup> selectedIons = precursor.getSelectedIons();
        if (index >= 0 && selectedIons != null && index < selectedIons.size()) {
            ParamGroup selectedIon = selectedIons.get(index);
            // search PRIDE xml based charge
            for (CvTermReference ref : refs) {
                List<CvParam> cvParams = getCvParam(selectedIon, ref.getCvLabel(), ref.getAccession());
                if (cvParams != null && !cvParams.isEmpty()) {
                    value = new Double(cvParams.get(0).getValue());
                }
            }
        }
        return value;
    }

    /**
     * This is convenient method for accessing peptide.
     *
     * @param ident identification object
     * @param index zero based index.
     * @return Peptide  peptide.
     */
    public static Peptide getPeptide(Identification ident, int index) {
        Peptide peptide = null;
        List<Peptide> peptides = ident.getPeptides();
        if (peptides != null && peptides.size() > index) {
            peptide = peptides.get(index);
        }

        return peptide;
    }

    /**
     * Convenient method for number of peptides
     *
     * @param ident identification object
     * @return int number of peptides
     */
    public static int getNumberOfPeptides(Identification ident) {
        List<Peptide> peptides = ident.getPeptides();
        return peptides == null ? 0 : peptides.size();
    }

    /**
     * Convenient method for getting the number of unique peptides.
     *
     * @param ident identification object
     * @return int  number of unique peptide sequences.
     */
    public static int getNumberOfUniquePeptides(Identification ident) {
        List<Peptide> peptides = ident.getPeptides();
        if (peptides == null) {
            return 0;
        } else {
            int cnt = 0;
            List<String> seqs = new ArrayList<String>();
            for (Peptide peptide : peptides) {
                String seq = peptide.getSequence();
                if (!seqs.contains(seq)) {
                    seqs.add(seq);
                    cnt++;
                }
            }
            return cnt;
        }
    }

    /**
     * Check whether spectrum has fragment ion information
     *
     * @param spectrum spectrum
     * @return boolean true if spectrum contains fragment ion information.
     */
    public static boolean hasFragmentIon(Spectrum spectrum) {
        Peptide peptide = spectrum.getPeptide();
        return peptide != null && hasFragmentIon(peptide);
    }


    /**
     * Check whether peptide has fragment ion information
     *
     * @param peptide peptide
     * @return boolean true if peptide contains fragment ion information.
     */
    public static boolean hasFragmentIon(Peptide peptide) {
        List<FragmentIon> ions = peptide.getFragmentIons();
        return ions != null && !ions.isEmpty();
    }

    public static int getNumberOfPTMs(Identification ident) {
        int cnt = 0;
        List<Peptide> peptides = ident.getPeptides();
        for (Peptide peptide : peptides) {
            List<Modification> mods = peptide.getModifications();
            if (mods != null) {
                cnt += mods.size();
            }
        }
        return cnt;
    }

    public static int getNumberOfPTMs(Peptide peptide) {
        int cnt = 0;

        List<Modification> mods = peptide.getModifications();
        if (mods != null) {
            cnt = mods.size();
        }

        return cnt;
    }

    /**
     * Get peptide score from a peptide object.
     *
     * @param paramGroup parameter group
     * @param seTypes    a list of search engine types
     * @return PeptideScore  peptide score
     */
    public static PeptideScore getPeptideScore(ParamGroup paramGroup, List<SearchEngineType> seTypes) {
        if (paramGroup == null || seTypes == null) {
            throw new IllegalArgumentException("Input arguments for getPeptideScore can not be null");
        }

        PeptideScore score = new PeptideScore();
        for (SearchEngineType type : seTypes) {
            List<CvTermReference> scoreCvTerms = type.getSearchEngineScores();
            for (CvTermReference scoreCvTerm : scoreCvTerms) {
                List<CvParam> scoreParams = getCvParam(paramGroup, scoreCvTerm.getCvLabel(), scoreCvTerm.getAccession());
                if (!scoreParams.isEmpty()) {
                    // Note: only take the first param as the valid score
                    CvParam scoreParam = scoreParams.get(0);
                    String numStr = scoreParam.getValue();
                    if (NumberUtilities.isNumber(numStr)) {
                        Double num = new Double(numStr);
                        score.addPeptideScore(type, scoreCvTerm, num);
                    } else {
                        score.addPeptideScore(type, scoreCvTerm, null);
                    }
                } else {
                    score.addPeptideScore(type, scoreCvTerm, null);
                }
            }
        }

        return score;
    }

    /**
     * Search and find a list of search engine types from input parameter group.
     *
     * @param paramGroup parameter group
     * @return List<SearchEngineType>  a list of search engine
     */
    public static List<SearchEngineType> getSearchEngineTypes(ParamGroup paramGroup) {
        if (paramGroup == null) {
            throw new IllegalArgumentException("Input argument for getSearchEngineTypes can not be null");
        }

        List<SearchEngineType> types = new ArrayList<SearchEngineType>();
        for (SearchEngineType type : SearchEngineType.values()) {
            for (CvTermReference scoreCvTerm : type.getSearchEngineScores()) {
                if (!getCvParam(paramGroup, scoreCvTerm.getCvLabel(), scoreCvTerm.getAccession()).isEmpty()) {
                    types.add(type);
                    break;
                }
            }
        }

        return types;
    }


    /**
     * Get cv param by accession number and cv label.
     *
     * @param paramGroup parameter group
     * @param cvLabel    cv label.
     * @param accession  cv accession.
     * @return CvParam  cv param.
     */
    public static List<CvParam> getCvParam(ParamGroup paramGroup, String cvLabel, String accession) {
        if (paramGroup == null || cvLabel == null || accession == null) {
            throw new IllegalArgumentException("Input arguments for getCvParam can not be null");
        }
        List<CvParam> cvParams = paramGroup.getCvParams();
        List<CvParam> cps = new ArrayList<CvParam>();
        if (cvParams != null) {
            for (CvParam param : cvParams) {
                if (param.getAccession().toLowerCase().equals(accession.toLowerCase())
                        && param.getCvLookupID().toLowerCase().equals(cvLabel.toLowerCase())) {
                    cps.add(param);
                }
            }
        }
        return cps;
    }

    /**
     * Get a list parameters using a given name.
     *
     * @param paramGroup parameter group
     * @param name       name string
     * @return List<Parameter> a list of parameters
     */
    public static List<Parameter> getParamByName(ParamGroup paramGroup, String name) {
        if (paramGroup == null || name == null) {
            throw new IllegalArgumentException("Input arguments for getParamByName can not be null");
        }
        List<Parameter> params = new ArrayList<Parameter>();

        List<CvParam> cvParams = paramGroup.getCvParams();
        if (cvParams != null) {
            for (CvParam cvParam : cvParams) {
                if (cvParam.getName().toLowerCase().equals(name.toLowerCase())) {
                    params.add(cvParam);
                }
            }
        }

        List<UserParam> userParams = paramGroup.getUserParams();
        if (userParams != null) {
            for (UserParam userParam : userParams) {
                if (userParam.getName().toLowerCase().equals(name.toLowerCase())) {
                    params.add(userParam);
                }
            }
        }

        return params;
    }
}
