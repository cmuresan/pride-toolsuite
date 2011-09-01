package uk.ac.ebi.pride.data.controller.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.coreIdent.DBSequence;
import uk.ac.ebi.pride.data.coreIdent.Identification;
import uk.ac.ebi.pride.data.coreIdent.SearchDataBase;
import uk.ac.ebi.pride.data.coreIdent.SearchEngine;

import java.util.Collection;

/**
 * IdentificationDataAccess defines the interface for accessing identification data.
 * It also defines identification related property changing event.
 * <p/>
 * User: rwang
 * Date: 29-Aug-2010
 * Time: 17:59:20
 */
public interface ProteinDataAccess {
    /**
     * fired when an identification object has changed
     */
    public static final String IDENTIFICATION_TYPE = "Identification";

    public static final String IDENTIFICATION_ID = "Identification ID";

    public static final String TWO_DIM_IDENTIFICATION_TYPE = "Two Dimensional";

    public static final String GEL_FREE_IDENTIFICATION_TYPE = "Gel Free";

    /**
     * Whether this controller contains identifications
     *
     * @return boolean  return true if identifications exist
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          throw a exception when there is an error
     *          accessing the data source
     */
    public boolean hasIdentification() throws DataAccessException;

    /**
     * Get a collection of identification ids
     *
     * @return Collection   a string collection of identification ids
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          throw a exception when there is an error
     *          accessing the data source
     */
    public Collection<Comparable> getIdentificationIds() throws DataAccessException;

    /**
     * Get the index of identification by its id
     *
     * @param id identification id
     * @return int  identification index
     * @throws DataAccessException data access exception
     */
    public int getIdentificationIndex(Comparable id) throws DataAccessException;

    /**
     * Get a Identification object
     *
     * @param id a string id of Identification
     * @return Identification an Identification object
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Identification getIdentificationById(Comparable id) throws DataAccessException;

    /**
     * Get the total number of identifications.
     *
     * @return int  the number of identifications.
     * @throws DataAccessException data access exception.
     */
    public int getNumberOfIdentifications() throws DataAccessException;

    /**
     * Get identifications by index, this combines both two dimensional and gel free identifications.
     * Note: this method can be used for paging.
     *
     * @param start  start index.
     * @param offset number of identification to get.
     * @return Collection<Identification> list of identifications.
     * @throws DataAccessException data access exception.
     */
    public Collection<Identification> getIdentificationsByIndex(int start, int offset) throws DataAccessException;

    /**
     * Get the protein accession of a identification
     *
     * @param identId identification id.
     * @return String   protein accession.
     * @throws DataAccessException data access exception.
     */
    public String getProteinAccession(Comparable identId) throws DataAccessException;

    /**
     * Get the protein accession version of a identification
     *
     * @param identId identification id.
     * @return String   protein accession version.
     * @throws DataAccessException data access exception.
     */
    public String getProteinAccessionVersion(Comparable identId) throws DataAccessException;

    /**
     * Get the type of the identification.
     *
     * @param identId identification id.
     * @return String   protein accession.
     * @throws DataAccessException data access exception.
     */
    public String getIdentificationType(Comparable identId) throws DataAccessException;

    /**
     * Get the score of a identification.
     *
     * @param identId identification id.
     * @return double   score.
     * @throws DataAccessException data access exception.
     */
    public double getIdentificationScore(Comparable identId) throws DataAccessException;

    /**
     * Get the threshold of a identification.
     *
     * @param identId identification id.
     * @return double   threshold.
     * @throws DataAccessException data access exception.
     */
    public double getIdentificationThreshold(Comparable identId) throws DataAccessException;

    /**
     * Get the search database of a identification
     *
     * @param identId identification id.
     * @return String   search database.
     * @throws DataAccessException data access exception.
     */
    public SearchDataBase getSearchDatabase(Comparable identId) throws DataAccessException;

    /**
     * Get the search engine of a identification
     *
     * @return String   search engine.
     * @throws DataAccessException data access exception.
     */
    public SearchEngine getSearchEngine() throws DataAccessException;

    /**
     * Get the sequence of the Identified Protein
     * @param identId identification Id
     * @return Sequence Object in the Database
     * @throws DataAccessException
     */
    public DBSequence getIdentificationSequence(Comparable identId) throws DataAccessException;




}
