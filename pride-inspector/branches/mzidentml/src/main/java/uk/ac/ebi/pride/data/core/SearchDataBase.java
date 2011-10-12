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
     * @param name
     * @param databaseVersion
     */
    public SearchDataBase(String name, String databaseVersion) {
        this(null, name, null, null, null, databaseVersion, null, -1, -1, null, null);
    }

    /**
     * @param id
     * @param name
     * @param location
     * @param fileFormat
     * @param externalFormatDocumentationURI
     * @param version
     * @param releaseDate
     * @param numDatabaseSequence
     * @param numDatabaseResidue
     * @param nameDatabase
     * @param description
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
        if (this == o) {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        SearchDataBase that = (SearchDataBase) o;

        if (numDatabaseResidue != that.numDatabaseResidue) {
            return false;
        }

        if (numDatabaseSequence != that.numDatabaseSequence) {
            return false;
        }

        if (!description.equals(that.description)) {
            return false;
        }

        if (!nameDatabase.equals(that.nameDatabase)) {
            return false;
        }

        if (!releaseDate.equals(that.releaseDate)) {
            return false;
        }

        if (!version.equals(that.version)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();

        result = 31 * result + releaseDate.hashCode();
        result = 31 * result + numDatabaseSequence;
        result = 31 * result + (int) (numDatabaseResidue ^ (numDatabaseResidue >>> 32));
        result = 31 * result + nameDatabase.hashCode();
        result = 31 * result + description.hashCode();

        return result;
    }
}



