package uk.ac.ebi.pride.data.controller.cache;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * CacheCategory provides a list of cache categories
 * Each category defines the type of the data and the type of data structure to store the data.
 * <p/>
 * User: rwang
 * Date: 07-Sep-2010
 * Time: 10:52:54
 */
public enum CacheCategory {

    SPECTRUM(CachedMap.class, 10), // Map<Spectrum id, Spectrum>
    CHROMATOGRAM(CachedMap.class, 10), // Map<Chromatogram id, Chromatogram>
    IDENTIFICATION(CachedMap.class, 10), // Map<Identification id, Identification>
    EXPERIMENT_ACC(ArrayList.class, null), // List<Experiement Accession>
    SPECTRUM_ID(ArrayList.class, null), // List<Spectrum id>
    CHROMATOGRAM_ID(ArrayList.class, null), // List<Chromatogram id>
    IDENTIFICATION_ID(ArrayList.class, null), // List<Identification id>
    MS_LEVEL(HashMap.class, null), // Map<Spectrum id, Ms level>
    PRECURSOR_CHARGE(HashMap.class, null), // Map<Spectrum id, Precursor charge>
    PRECURSOR_MZ(HashMap.class, null), // Map<Spectrum id, Precursor m/z>
    PRECURSOR_INTENSITY(HashMap.class, null), // Map<Spectrum id, Precursor intensity>
    PROTEIN_ACCESSION(HashMap.class, null), // Map<Identification id, Protein accession>
    PROTEIN_ACCESSION_VERSION(HashMap.class, null), // Map<Identification id, Protein accession version>
    PROTEIN_SEARCH_DATABASE(HashMap.class, null), // Map<Identification id, Protein search database>
    SCORE(HashMap.class, null), // Map<Identification id, Score>
    THRESHOLD(HashMap.class, null), // Map<Identification id, Threshold>
    IDENTIFICATION_TO_PEPTIDE(HashMap.class, null), // Map<Identification id, List<Peptide id>>
    PEPTIDE_SEQUENCE(HashMap.class, null), // Map<Peptide Id, peptide sequence>
    PEPTIDE_START(HashMap.class, null), // Map<Peptide Id, peptide start location>
    PEPTIDE_END(HashMap.class, null), // Map<Peptide Id, peptide end location>
    PEPTIDE_TO_SPECTRUM(HashMap.class, null), // Map<Peptide Id, spectrum id>
    PEPTIDE_TO_PARAM(HashMap.class, null),// Map<Peptide Id, ParamGroup>
    NUMBER_OF_FRAGMENT_IONS(HashMap.class, null), // Map<Peptide Id, number of fragment ions>
    PEPTIDE_TO_MODIFICATION(HashMap.class, null), // Map<Peptide Id, List<Tuple<Accession, location>>>
    MODIFICATION(HashMap.class, null), // Map<Accession, Modification>, a light weight implementation
    SUM_OF_INTENSITY(HashMap.class, null), // Map<Spectrum id, sum of all intensity>
    NUMBER_OF_PEAKS(HashMap.class, null); // Map<Spectrum id, number of peaks>

    private Class dataStructType;
    private Integer size;

    private CacheCategory(Class dataStructType, Integer size) {
        this.dataStructType = dataStructType;
        this.size = size;
    }

    public Class getDataStructType() {
        return dataStructType;
    }

    public Integer getSize() {
        return size;
    }
}