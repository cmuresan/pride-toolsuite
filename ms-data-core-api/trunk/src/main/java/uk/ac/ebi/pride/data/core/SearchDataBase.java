package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * A database for searching mass spectra. Examples include a set of amino
 * acid sequence entries, or annotated spectra libraries.
 * <p>The SearchDataBase Element contains contains the following members:
 * - version: Database Version
 * - Release Database
 * - name of the database: CvTerms and CvParm
 * - Description of the DataBase.
 * </p>
 * User: yperez
 * Date: 08/08/11
 * Time: 11:52
 */
public class SearchDataBase extends ExternalData {

    /**
     * Description of the database with CVTerms
     */
    private List<CvParam> description = null;

    /**
     * The database name may be given as a cvParam if it maps exactly to one
     * of the release databases listed in the CV, otherwise a userParam should be
     * used.
     */
    private ParamGroup nameDatabase = null;

    /**
     * The number of residues in the database.
     */
    private long numDatabaseResidue = -1;

    /**
     * The total number of sequences in the database.
     */
    private int numDatabaseSequence = -1;

    /**
     * The date and time the database was released to the public; omit this
     * attribute when the date and time are unknown or not applicable
     * (e.g. custom databases).
     */
    private String releaseDate = null;

    /**
     * The version of the database.
     */
    private String version = null;

    /**
     * Constructor for Pride SearchDatabase Object
     *
     * @param name Name of the Database
     * @param databaseVersion Version of the Database
     */
    public SearchDataBase(String name, String databaseVersion) {
        this(null, name, null, null, null, databaseVersion, null, -1, -1, null, null);
    }

    /**
     * @param id           Generic Id of SearchDatabase
     * @param name         Generic Name of SearchDatabase
     * @param location     Location
     * @param fileFormat   File format of the SearchDatabase in CvTerm
     * @param externalFormatDocumentationURI External Format Documentation in CvTerm
     * @param version                        Database Version
     * @param releaseDate                    Date Release
     * @param numDatabaseSequence            Number of Database Sequences
     * @param numDatabaseResidue             Number of Database Residues
     * @param nameDatabase                   Database Name
     * @param description                    Database Description
     */
    public SearchDataBase(String id, String name, String location, CvParam fileFormat,
                          String externalFormatDocumentationURI, String version, String releaseDate,
                          int numDatabaseSequence, long numDatabaseResidue, ParamGroup nameDatabase,
                          List<CvParam> description) {
        super(id, name, location, fileFormat, externalFormatDocumentationURI);
        this.version             = version;
        this.releaseDate         = releaseDate;
        this.numDatabaseSequence = numDatabaseSequence;
        this.numDatabaseResidue  = numDatabaseResidue;
        this.nameDatabase        = nameDatabase;
        this.description         = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getNumDatabaseSequence() {
        return numDatabaseSequence;
    }

    public void setNumDatabaseSequence(int numDatabaseSequence) {
        this.numDatabaseSequence = numDatabaseSequence;
    }

    public long getNumDatabaseResidue() {
        return numDatabaseResidue;
    }

    public void setNumDatabaseResidue(long numDatabaseResidue) {
        this.numDatabaseResidue = numDatabaseResidue;
    }

    public ParamGroup getNameDatabase() {
        return nameDatabase;
    }

    public void setNameDatabase(ParamGroup nameDatabase) {
        this.nameDatabase = nameDatabase;
    }

    public List<CvParam> getDescription() {
        return description;
    }

    public void setDescription(List<CvParam> description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchDataBase that = (SearchDataBase) o;

        return numDatabaseResidue == that.numDatabaseResidue && numDatabaseSequence == that.numDatabaseSequence && !(description != null ? !description.equals(that.description) : that.description != null) && !(nameDatabase != null ? !nameDatabase.equals(that.nameDatabase) : that.nameDatabase != null) && !(releaseDate != null ? !releaseDate.equals(that.releaseDate) : that.releaseDate != null) && !(version != null ? !version.equals(that.version) : that.version != null);

    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (nameDatabase != null ? nameDatabase.hashCode() : 0);
        result = 31 * result + (int) (numDatabaseResidue ^ (numDatabaseResidue >>> 32));
        result = 31 * result + numDatabaseSequence;
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}



