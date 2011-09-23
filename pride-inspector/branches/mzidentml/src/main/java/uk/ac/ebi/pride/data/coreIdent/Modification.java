package uk.ac.ebi.pride.data.coreIdent;

import java.util.List;

/**
 * A molecule modification specification. If n modifications have been found on a peptide,
 * there should be n instances of Modification. If multiple modifications are provided as cvParams,
 * it is assumed that the modification is ambiguous i.e. one modification or another.
 * A ParamGroup must be provided with the identification of the modification sourced from a
 * suitable CV e.g. UNIMOD. If the modification is not present in the CV (and this will be checked
 * by the semantic validator within a given tolerance window), there is a “unknown modification”
 * CV term that must be used instead. A neutral loss should be defined as an additional CVParam
 * within Modification. If more complex information should be given about neutral losses
 * (such as presence/absence on particular product ions), this can additionally be encoded within
 * the FragmentationArray.
 * <p>
 *     The modification for PRIDE Model and MZIdentMl model have the following features:
 *         - id: Modification identifier
 *         - name: Modification Name.
 *         - location: Location of the modification within the peptide - position in peptide sequence.
 *         - residues: Specification of the residue (amino acid) on which the modification occurs.
 *         - Average Mass Delta: Atomic mass delta considering the natural distribution of isotopes in Daltons.
 *         - Average MonoIsotopic Mass Delta: Atomic mass delta when assuming only the most common isotope of elements in Daltons.
 *         - Modification database where accession is from (used for PRIDE Objects)
 *         - Modification database version is (used for PRIDE Objects)
 * </p>
 * Created by IntelliJ IDEA.
 * User: yperez, rwang
 * Date: 04/08/11
 * Time: 14:11
 */
public class Modification extends IdentifiableParamGroup{
    /**
     * Location of the modification within the peptide - position in peptide sequence, counted from
     * the N-terminus residue, starting at position 1. Specific modifications to the N-terminus should be
     * given the location 0. Modification to the C-terminus should be given as peptide length + 1.
     * MzIdentMl and PrideXML
     */
    private int location = -1;
    /**
     * Possible Residues for this modification. In the PRIDE Object this attribute do not exist but in the
     * pride modification validator One modification can be related with more than one specificity. In MzIdentML
     * Object the Modification is related with more than one specificity.
     */
    private List<String> residues = null;
    /**
     * In the new validation approach for pride modification objects, just one Average Mass Delta could be associated
     * to a Modification. In the MzIdentMl Modification object only one Average Mass Delta is annotated.
     */
    private List<Double> avgMassDelta = null;
    /**
     * In the new validation approach for pride modification objects, just one MonoIsotopic Mass Delta could be associated
     * to a Modification. In the MzIdentMl Modification object only one MonoIsotopic Mass Delta is annotated.
     */
    private List<Double> monoisotopicMassDelta = null;
    /**
     * modification database where accession is from (used for PRIDE Objects)
     */
    private String modDatabase = null;
    /**
     * modification database version is (used for PRIDE Objects)
     */
    private String modDatabaseVersion = null;

    /**
     * Constructor for MzIdentMl Modification Object
     * @param id
     * @param name
     * @param location
     * @param residues
     * @param avgMassDelta
     * @param monoisotopicMassDelta
     */
    public Modification(String id,
                        String name,
                        int location,
                        List<String> residues,
                        List<Double> avgMassDelta,
                        List<Double> monoisotopicMassDelta) {
        super(id, name);
        this.location = location;
        this.residues = residues;
        this.avgMassDelta = avgMassDelta;
        this.monoisotopicMassDelta = monoisotopicMassDelta;
    }

    /**
     * Constructor for MzIdentMl Modification Object
     * @param params
     * @param id
     * @param name
     * @param location
     * @param residues
     * @param avgMassDelta
     * @param monoisotopicMassDelta
     */
    public Modification(ParamGroup params,
                        String id,
                        String name,
                        int location,
                        List<String> residues,
                        List<Double> avgMassDelta,
                        List<Double> monoisotopicMassDelta) {
        super(params, id, name);
        this.location = location;
        this.residues = residues;
        this.avgMassDelta = avgMassDelta;
        this.monoisotopicMassDelta = monoisotopicMassDelta;
    }

    /**
     * Constructor for MzIdentMl Modification Object
     * @param cvParams
     * @param userParams
     * @param id
     * @param name
     * @param location
     * @param residues
     * @param avgMassDelta
     * @param monoisotopicMassDelta
     */
    public Modification(List<CvParam> cvParams,
                        List<UserParam> userParams,
                        String id,
                        String name,
                        int location,
                        List<String> residues,
                        List<Double> avgMassDelta,
                        List<Double> monoisotopicMassDelta) {
        super(cvParams, userParams, id, name);
        this.location = location;
        this.residues = residues;
        this.avgMassDelta = avgMassDelta;
        this.monoisotopicMassDelta = monoisotopicMassDelta;
    }

    /**
     * Constructor for PRIDE Modification Object
     * @param id
     * @param name
     * @param location
     * @param residues
     * @param avgMassDelta
     * @param monoisotopicMassDelta
     * @param modDatabase
     * @param modDatabaseVersion
     */
    public Modification(String id,
                        String name,
                        int location,
                        List<String> residues,
                        List<Double> avgMassDelta,
                        List<Double> monoisotopicMassDelta,
                        String modDatabase,
                        String modDatabaseVersion) {
        super(id, name);
        this.location = location;
        this.residues = residues;
        this.avgMassDelta = avgMassDelta;
        this.monoisotopicMassDelta = monoisotopicMassDelta;
        this.modDatabase = modDatabase;
        this.modDatabaseVersion = modDatabaseVersion;
    }

    /**
     * Constructor for PRIDE Modification Object
     * @param params
     * @param id
     * @param name
     * @param location
     * @param residues
     * @param avgMassDelta
     * @param monoisotopicMassDelta
     * @param modDatabase
     * @param modDatabaseVersion
     */
    public Modification(ParamGroup params,
                        String id,
                        String name,
                        int location,
                        List<String> residues,
                        List<Double> avgMassDelta,
                        List<Double> monoisotopicMassDelta,
                        String modDatabase,
                        String modDatabaseVersion) {
        super(params, id, name);
        this.location = location;
        this.residues = residues;
        this.avgMassDelta = avgMassDelta;
        this.monoisotopicMassDelta = monoisotopicMassDelta;
        this.modDatabase = modDatabase;
        this.modDatabaseVersion = modDatabaseVersion;
    }

    /**
     * Constructor for PRIDE Modification Object
     * @param cvParams
     * @param userParams
     * @param id
     * @param name
     * @param location
     * @param residues
     * @param avgMassDelta
     * @param monoisotopicMassDelta
     * @param modDatabase
     * @param modDatabaseVersion
     */
    public Modification(List<CvParam> cvParams,
                        List<UserParam> userParams,
                        String id,
                        String name,
                        int location,
                        List<String> residues,
                        List<Double> avgMassDelta,
                        List<Double> monoisotopicMassDelta,
                        String modDatabase,
                        String modDatabaseVersion) {
        super(cvParams, userParams, id, name);
        this.location = location;
        this.residues = residues;
        this.avgMassDelta = avgMassDelta;
        this.monoisotopicMassDelta = monoisotopicMassDelta;
        this.modDatabase = modDatabase;
        this.modDatabaseVersion = modDatabaseVersion;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public List<String> getResidues() {
        return residues;
    }

    public void setResidues(List<String> residues) {
        this.residues = residues;
    }

    public String getModDatabase() {
        return modDatabase;
    }

    public void setModDatabase(String modDatabase) {
        this.modDatabase = modDatabase;
    }

    public String getModDatabaseVersion() {
        return modDatabaseVersion;
    }

    public void setModDatabaseVersion(String modDatabaseVersion) {
        this.modDatabaseVersion = modDatabaseVersion;
    }

    public List<Double> getAvgMassDelta() {
        return avgMassDelta;
    }

    public void setAvgMassDelta(List<Double> avgMassDelta) {
        this.avgMassDelta = avgMassDelta;
    }

    public List<Double> getMonoisotopicMassDelta() {
        return monoisotopicMassDelta;
    }

    public void setMonoisotopicMassDelta(List<Double> monoisotopicMassDelta) {
        this.monoisotopicMassDelta = monoisotopicMassDelta;
    }
}
