package uk.ac.ebi.pride.data.controller.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.term.CvTermReference;

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
     * The name for gel free identification
     */
    public static final String GEL_FREE_PROTEIN_IDENTIFICATION_TYPE = "Gel Free";

    /**
     * The name for two dimensional identification
     */
    public static final String TWO_DIM_PROTEIN_IDENTIFICATION_TYPE = "Two Dimensional";

    /**
     * Whether this controller contains identifications
     *
     * @return boolean  return true if identifications exist
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          throw a exception when there is an error
     *          accessing the data source
     */
    public boolean hasProtein() throws DataAccessException;


    /**
        * Whether this controller contains protein groups information
        *
        * @return boolean  return true if identifications exist
        * @throws uk.ac.ebi.pride.data.controller.DataAccessException
        *          throw a exception when there is an error
        *          accessing the data source
        */
   public boolean hasProteinGroup() throws  DataAccessException;

  /**
     * Get a collection of identification ids
     *
     * @return Collection   a string collection of identification ids
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          throw a exception when there is an error
     *          accessing the data source
     */
   public Collection<Comparable> getProteinIds() throws DataAccessException;

    /**
     * Get a collection of identification group ids
     *
     * @return Collection   a string collection of identification ids
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          throw a exception when there is an error
     *          accessing the data source
     */
    public Collection<Comparable> getProteinGroupIds() throws DataAccessException;

    /**
     * Get the index of identification by its id
     *
     * @param proteinId identification id
     * @return int  identification index
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception
     */
    public int indexOfProtein(Comparable proteinId) throws DataAccessException;

    /**
     * Get a Identification object
     *
     * @param proteinId a string id of Identification
     * @return Identification an Identification object
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException throw a exception when there is an error accessing the data source
     */
    public Protein getProteinById(Comparable proteinId) throws DataAccessException;

    /**
     * Get the total number of identifications.
     *
     * @return int  the number of identifications.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception.
     */
    public int getNumberOfProteins() throws DataAccessException;

    /**
     * Get identifications by index, this combines both two dimensional and gel free identifications.
     * Note: this method can be used for paging.
     *
     * @param start  start index.
     * @param offset number of identification to get.
     * @return Collection<Identification> list of identifications.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception.
     */
    public Collection<Protein> getProteinByIndex(int start, int offset) throws DataAccessException;

    /**
     * Get the protein accession of a identification
     *
     * @param proteinId identification id.
     * @return String   protein accession.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception.
     */
    public String getProteinAccession(Comparable proteinId) throws DataAccessException;

    /**
     * Get the protein accession version of a identification
     *
     * @param proteinId identification id.
     * @return String   protein accession version.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception.
     */
    public String getProteinAccessionVersion(Comparable proteinId) throws DataAccessException;

    /**
     * Get the type of the identification.
     *
     * @param proteinId identification id.
     * @return String   protein accession.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception.
     */
    public String getProteinType(Comparable proteinId) throws DataAccessException;

    /**
     * Get the score of a identification.
     *
     * @param proteinId identification id.
     * @return double   score.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception.
     */
    public double getProteinScore(Comparable proteinId) throws DataAccessException;

    /**
     * Get protein identification score
     *
     * @param proteinId   Protein Id
     * @return  Protein Score List
     * @throws DataAccessException
     */
    public Score getProteinScores(Comparable proteinId) throws DataAccessException;

    /**
     * Get the threshold of a identification.
     *
     * @param proteinId identification id.
     * @return double   threshold.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception.
     */
    public double getProteinThreshold(Comparable proteinId) throws DataAccessException;

    /**
     * Get the search database of a identification
     *
     * @param proteinId identification id.
     * @return String   search database.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception.
     */
    public SearchDataBase getSearchDatabase(Comparable proteinId) throws DataAccessException;

    /**
     * Get the search engine of a identification
     *
     * @return String   search engine.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException data access exception.
     */
    public SearchEngine getSearchEngine() throws DataAccessException;

    /**
     * Get the search database version of a identification
     * @param proteinId   identification id
     * @return  String  search database version
     * @throws DataAccessException  data access exception
     */
    public String getSearchDatabaseVersion(Comparable proteinId) throws DataAccessException;

    /**
     * get a list of present protein scores in CV terms
     *
     * @return List of Protein Scores in CvTerm
     * @throws DataAccessException
     *
     * todo: this name needs to be changed
     */
    public Collection<CvTermReference> getProteinCvTermReferenceScores() throws  DataAccessException;

    /**
     * Get the sequence of the Identified Protein
     *
     * @param proteinId identification Id
     * @return Sequence Object in the Database
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     */
    public DBSequence getProteinSequence(Comparable proteinId) throws DataAccessException;

}



