package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Descriptions of peptide modification.
 * <p/>
 * User: rwang
 * Date: 08-Feb-2010
 * Time: 12:32:12
 */
public class Modification extends ParamGroup {
    /**
     * modification accession
     */
    private String accession = null;
    /**
     * modification name
     */
    private String name = null;
    /**
     * modification database where accession is from
     */
    private String modDatabase = null;
    /**
     * modification database version
     */
    private String modDatabaseVersion = null;
    /**
     * a list of ModMonoDelta
     */
    private List<Double> monoMassDeltas = null;
    /**
     * a list of ModAvgDelta
     */
    private List<Double> avgMassDeltas = null;
    /**
     * modification location
     */
    private int location = -1;

    /**
     * Constructor
     *
     * @param params             optional.
     * @param accession          required.
     * @param modDatabase        required.
     * @param modDatabaseVersion optional.
     * @param monoMassDeltas     optional.
     * @param avgMassDeltas      optional.
     * @param location           required.
     */
    public Modification(ParamGroup params,
                        String accession,
                        String modDatabase,
                        String modDatabaseVersion,
                        List<Double> monoMassDeltas,
                        List<Double> avgMassDeltas,
                        int location) {
        super(params);
        setAccession(accession);
        setModDatabase(modDatabase);
        setModDatabaseVersion(modDatabaseVersion);
        setMonoMassDeltas(monoMassDeltas);
        setAvgMassDeltas(avgMassDeltas);
        setLocation(location);
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getName() {
        if (name == null) {
            List<CvParam> cvParams = this.getCvParams();
            if (cvParams != null) {
                for (CvParam cvParam : cvParams) {
                    if (cvParam.getAccession().equals(accession)) {
                        name = cvParam.getName();
                    }
                }
            }
        }
        return name;
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

    public List<Double> getMonoMassDeltas() {
        return monoMassDeltas;
    }

    public void setMonoMassDeltas(List<Double> monoMassDeltas) {
        this.monoMassDeltas = monoMassDeltas;
    }

    public List<Double> getAvgMassDeltas() {
        return avgMassDeltas;
    }

    public void setAvgMassDeltas(List<Double> avgMassDeltas) {
        this.avgMassDeltas = avgMassDeltas;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}
