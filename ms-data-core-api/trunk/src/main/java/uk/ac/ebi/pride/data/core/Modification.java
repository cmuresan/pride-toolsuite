package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

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
 * The modification for PRIDE Model and MZIdentMl model have the following features:
 * - id: Modification identifier
 * - name: Modification Name.
 * - location: Location of the modification within the peptide - position in peptide sequence.
 * - residues: Specification of the residue (amino acid) on which the modification occurs.
 * - Average Mass Delta: Atomic mass delta considering the natural distribution of isotopes in Daltons.
 * - Average MonoIsotopic Mass Delta: Atomic mass delta when assuming only the most common isotope of elements in Daltons.
 * - Modification database where accession is from (used for PRIDE Objects)
 * - Modification database version is (used for PRIDE Objects)
 * </p>
 * Created by IntelliJ IDEA.
 * User: yperez, rwang
 * Date: 04/08/11
 * Time: 14:11
 */
public class Modification extends IdentifiableParamGroup {

    /**
     * In the new validation approach for pride modification objects, just one Average Mass Delta could be associated
     * to a Modification. In the MzIdentMl Modification object only one Average Mass Delta is annotated.
     */
    private List<Double> avgMassDelta = null;

    /**
     * Location of the modification within the peptide - position in peptide sequence, counted from
     * the N-terminus residue, starting at position 1. Specific modifications to the N-terminus should be
     * given the location 0. Modification to the C-terminus should be given as peptide length + 1.
     * MzIdentMl and PrideXML
     */
    private int location = -1;

    /**
     * modification database where accession is from (used for PRIDE Objects)
     */
    private String modDatabase = null;

    /**
     * modification database version is (used for PRIDE Objects)
     */
    private String modDatabaseVersion = null;

    /**
     * In the new validation approach for pride modification objects, just one MonoIsotopic Mass Delta could be associated
     * to a Modification. In the MzIdentMl Modification object only one MonoIsotopic Mass Delta is annotated.
     */
    private List<Double> monoisotopicMassDelta = null;

    /**
     * Possible Residues for this modification. In the PRIDE Object this attribute do not exist but in the
     * pride modification validator One modification can be related with more than one specificity. In MzIdentML
     * Object the Modification is related with more than one specificity.
     */
    private List<String> residues = null;

    /**
     * Constructor for PRIDE Modification Object
     *
     * @param id ID
     * @param name Name
     * @param location Location
     * @param residues List of the possible residues where the modification is present
     * @param avgMassDelta List of Possible Average Mass Delta
     * @param monoisotopicMassDelta List of Possible MonoIsotopic Mass Delta
     * @param modDatabase DataBase Name
     * @param modDatabaseVersion DataBase Version
     */
    public Modification(String id, String name, int location, List<String> residues, List<Double> avgMassDelta,
                        List<Double> monoisotopicMassDelta, String modDatabase, String modDatabaseVersion) {
        super(id, name);
        this.location              = location;
        this.residues              = residues;
        this.avgMassDelta          = avgMassDelta;
        this.monoisotopicMassDelta = monoisotopicMassDelta;
        this.modDatabase           = modDatabase;
        this.modDatabaseVersion    = modDatabaseVersion;
    }

    /**
     * Constructor for Modification Objects
     *
     * @param params ParamGroup (CvTerms and User Params)
     * @param id ID
     * @param name Name
     * @param location Location
     * @param residues List of the possible residues where the modification is present
     * @param avgMassDelta List of Possible Average Mass Delta
     * @param monoisotopicMassDelta List of Possible MonoIsotopic Mass Delta
     * @param modDatabase DataBase Name
     * @param modDatabaseVersion DataBase Version
     */
    public Modification(ParamGroup params, String id, String name, int location, List<String> residues,
                        List<Double> avgMassDelta, List<Double> monoisotopicMassDelta, String modDatabase,
                        String modDatabaseVersion) {
        super(params, id, name);
        this.location              = location;
        this.residues              = residues;
        this.avgMassDelta          = avgMassDelta;
        this.monoisotopicMassDelta = monoisotopicMassDelta;
        this.modDatabase           = modDatabase;
        this.modDatabaseVersion    = modDatabaseVersion;
    }

    /**
     * Get Location of the Modification
     *
     * @return Location
     */
    public int getLocation() {
        return location;
    }

    /**
     * Set Location of the Modification
     *
     * @param location Location
     */
    public void setLocation(int location) {
        this.location = location;
    }

    /**
     * Get the Amino Acids associated with this modification
     *
     * @return List of Residues (Amino Acids)
     */
    public List<String> getResidues() {
        return residues;
    }

    /**
     * Set the Amino Acids associated with this modification
     *
     * @param residues List of Residues (Amino Acids)
     */
    public void setResidues(List<String> residues) {
        this.residues = residues;
    }

    /**
     * Get Modification DataBase Name
     *
     * @return DataBase Name
     */
    public String getModDatabase() {
        return modDatabase;
    }

    /**
     * Set Modification DataBase Name
     *
     * @param modDatabase DataBase Name
     */
    public void setModDatabase(String modDatabase) {
        this.modDatabase = modDatabase;
    }

    /**
     * Get Modification DataBase Version
     *
     * @return DataBase Version
     */
    public String getModDatabaseVersion() {
        return modDatabaseVersion;
    }

    /**
     * Set Modification DataBase Version
     *
     * @param modDatabaseVersion DataBase Version
     */
    public void setModDatabaseVersion(String modDatabaseVersion) {
        this.modDatabaseVersion = modDatabaseVersion;
    }

    /**
     * Get Average Mass Delta List
     *
     * @return Average Mass Delta List
     */
    public List<Double> getAvgMassDelta() {
        return avgMassDelta;
    }

    /**
     * Get Average Mass Delta List
     *
     * @param avgMassDelta Average Mass Delta List
     */
    public void setAvgMassDelta(List<Double> avgMassDelta) {
        this.avgMassDelta = avgMassDelta;
    }

    /**
     * Get monoisotopic Mass Delta List
     *
     * @return monoisotopic Mass Delta List
     */
    public List<Double> getMonoisotopicMassDelta() {
        return monoisotopicMassDelta;
    }

    /**
     * Set monoisotopic mass delta List
     *
     * @param monoisotopicMassDelta monoisotopic mass delta List
     */
    public void setMonoisotopicMassDelta(List<Double> monoisotopicMassDelta) {
        this.monoisotopicMassDelta = monoisotopicMassDelta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Modification that = (Modification) o;

        return location == that.location && !(avgMassDelta != null ? !avgMassDelta.equals(that.avgMassDelta) : that.avgMassDelta != null) && !(modDatabase != null ? !modDatabase.equals(that.modDatabase) : that.modDatabase != null) && !(modDatabaseVersion != null ? !modDatabaseVersion.equals(that.modDatabaseVersion) : that.modDatabaseVersion != null) && !(monoisotopicMassDelta != null ? !monoisotopicMassDelta.equals(that.monoisotopicMassDelta) : that.monoisotopicMassDelta != null) && !(residues != null ? !residues.equals(that.residues) : that.residues != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (avgMassDelta != null ? avgMassDelta.hashCode() : 0);
        result = 31 * result + location;
        result = 31 * result + (modDatabase != null ? modDatabase.hashCode() : 0);
        result = 31 * result + (modDatabaseVersion != null ? modDatabaseVersion.hashCode() : 0);
        result = 31 * result + (monoisotopicMassDelta != null ? monoisotopicMassDelta.hashCode() : 0);
        result = 31 * result + (residues != null ? residues.hashCode() : 0);
        return result;
    }
}



