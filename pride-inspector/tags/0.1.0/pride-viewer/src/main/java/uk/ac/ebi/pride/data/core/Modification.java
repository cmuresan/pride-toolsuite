package uk.ac.ebi.pride.data.core;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 08-Feb-2010
 * Time: 12:32:12
 */
public class Modification extends ParamGroup {
    /** modification accession */
    private String accession = null;
    /** modification database where accession is from */
    private String ModDatabase = null;
    /** modifcation database version */
    private String ModDatabaseVersion = null;
    /** a list of ModMonoDelta */
    private List<Double> monoMassDeltas = null;
    /** a list of ModAvgDelta */
    private List<Double> avgMassDeltas = null;
    /** modification location */
    private BigInteger location = null;

    public Modification(ParamGroup params, String accession,
                        String modDatabase, String modDatabaseVersion,
                        List<Double> monoMassDeltas, List<Double> avgMassDeltas,
                        BigInteger location) {
        super(params);
        this.accession = accession;
        ModDatabase = modDatabase;
        ModDatabaseVersion = modDatabaseVersion;
        this.monoMassDeltas = monoMassDeltas;
        this.avgMassDeltas = avgMassDeltas;
        this.location = location;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getModDatabase() {
        return ModDatabase;
    }

    public void setModDatabase(String modDatabase) {
        ModDatabase = modDatabase;
    }

    public String getModDatabaseVersion() {
        return ModDatabaseVersion;
    }

    public void setModDatabaseVersion(String modDatabaseVersion) {
        ModDatabaseVersion = modDatabaseVersion;
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

    public BigInteger getLocation() {
        return location;
    }

    public void setLocation(BigInteger location) {
        this.location = location;
    }
}
