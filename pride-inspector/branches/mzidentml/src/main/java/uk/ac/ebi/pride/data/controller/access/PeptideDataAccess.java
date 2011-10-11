package uk.ac.ebi.pride.data.controller.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;

import java.util.Collection;

/**
 * PeptideDataAccess defines methods for accessing peptide related information.
 * <p/>
 * You may find the concept of peptide id alien to PRIDE XML files, the index of peptide in
 * the identification should be used in this case.
 * <p/>
 * User: rwang
 * Date: 03-Sep-2010
 * Time: 10:37:47
 */
public interface PeptideDataAccess {
    /**
     * Whether this controller contains peptides
     *
     * @return boolean  return true if peptide exists
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          throw a exception when there is an error
     *          accessing the data source
     */
    public boolean hasPeptide() throws DataAccessException;

    /**
     * Get a collection of peptide ids belong to the identification.
     *
     * @param identId identification id
     * @return Collection<Comparable>   peptide ids
     * @throws DataAccessException data access exception
     */
    public Collection<Comparable> getPeptideIds(Comparable identId) throws DataAccessException;

    /**
     * This is convenient method for accessing peptide.
     *
     * @param identId   identification id
     * @param peptideId peptide id, this can be the index of the peptide as well.
     * @return Peptide  peptide.
     * @throws DataAccessException data access exception.
     */
    public Peptide getPeptideById(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * This method is to get a list of redundant peptide sequences
     *
     * @param identId identification id
     * @return Collection<String>   return a list of peptide sequences.
     * @throws DataAccessException data access exception.
     */
    public Collection<String> getPeptideSequences(Comparable identId) throws DataAccessException;

    /**
     * Get peptide sequence according to identification id and peptide id.
     *
     * @param identId   identification id
     * @param peptideId peptide id, this can be the index of the peptide as well.
     * @return String   peptide sequence
     * @throws DataAccessException data access exception
     */
    public String getPeptideSequence(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get peptide sequence start
     *
     * @param identId   identification id
     * @param peptideId peptide id, this can be the index of the peptide as well.
     * @return int  start position for the peptide
     * @throws DataAccessException data accession exception
     */
    public int getPeptideSequenceStart(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get peptide sequence stop
     *
     * @param identId   identification id
     * @param peptideId peptide id, this can be the index of the peptide as well.
     * @return int  stop position for the peptide
     * @throws DataAccessException data access exception
     */
    public int getPeptideSequenceEnd(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get peptide's spectrum id
     *
     * @param identId   identification id
     * @param peptideId peptide id, this can be the index of the peptide as well.
     * @return Comparable   spectrum reference.
     * @throws DataAccessException data access exception
     */
    public Comparable getPeptideSpectrumId(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get the total number of peptides
     *
     * @return int  total number of peptides.
     * @throws DataAccessException data access exception
     */
    public int getNumberOfPeptides() throws DataAccessException;

    /**
     * Get the number peptides of a identification.
     *
     * @param identId identification id.
     * @return int  number of peptides.
     * @throws DataAccessException data access exception.
     */
    public int getNumberOfPeptides(Comparable identId) throws DataAccessException;

    /**
     * Get the number of unique peptides of a identification.
     *
     * @param identId identification id.
     * @return int  number of unique peptides.
     * @throws DataAccessException data access exception.
     */
    public int getNumberOfUniquePeptides(Comparable identId) throws DataAccessException;

    /**
     * Get the number of ptms of a identification.
     * Note: this is not unique number of PTMs.
     *
     * @param identId identification id.
     * @return int  the number of PTMs.
     * @throws DataAccessException data access exception.
     */
    public int getNumberOfPTMs(Comparable identId) throws DataAccessException;

    /**
     * Get the number of PTMs for a peptide
     *
     * @param identId   identification id
     * @param peptideId peptide id
     * @return int  number of ptms
     * @throws DataAccessException data access exception
     */
    public int getNumberOfPTMs(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get the ptms assigned to a peptide
     *
     * @param identId   identification id
     * @param peptideId peptide id, can be the index of the peptide
     * @return Collection<Modification> a collection of ptms
     * @throws DataAccessException data access exception
     */
    public Collection<Modification> getPTMs(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get the number of Substitution ptms of a identification.
     * Note: this is not unique number of PTMs.
     *
     * @param identId identification id.
     * @return int  the number of PTMs.
     * @throws DataAccessException data access exception.
     */
    public int getNumberOfSubstitutionPTMs(Comparable identId) throws DataAccessException;

    /**
     * Get the number of Substitution PTMs for a peptide
     *
     * @param identId   identification id
     * @param peptideId peptide id
     * @return int  number of ptms
     * @throws DataAccessException data access exception
     */

    public int getNumberOfSubstitutionPTMs(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get the ptms assigned to a peptide
     *
     * @param identId   identification id
     * @param peptideId peptide id, can be the index of the peptide
     * @return Collection<Modification> a collection of ptms
     * @throws DataAccessException data access exception
     */
    public Collection<SubstitutionModification> getSubstitutionPTMs(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get the number of fragment ions for a peptide
     *
     * @param identId   identification id
     * @param peptideId peptide id, can be the index of the peptide as well.
     * @return int  number of fragment ions
     * @throws DataAccessException data access exception
     */
    public int getNumberOfFragmentIons(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get the fragment ions assigned to the peptide.
     *
     * @param identId   identification id
     * @param peptideId peptide id, can be the index of the peptide as well.
     * @return Collection<FragmentIon>  a collection of fragment ions.
     * @throws DataAccessException data access exception.
     */
    public Collection<FragmentIon> getFragmentIons(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get peptide score generated by search engine.
     *
     * @param identId   identification id
     * @param peptideId peptide id, can be the index of the peptide as well.
     * @return  PeptideScore  peptide score
     * @throws DataAccessException  data access exception
     */
    public PeptideScore getPeptideScore(Comparable identId, Comparable peptideId) throws DataAccessException;

    /**
     * Get all Peptide Evidence for a Peptide Identification
     * @param identId   identification id
     * @param peptideId  peptide id, can be the index of the peptide as well.
     * @return Collection<PeptideEvidence> collection of peptide Evidences.
     * @throws DataAccessException
     */
    public Collection<PeptideEvidence> getPeptideEvidences(Comparable identId, Comparable peptideId) throws DataAccessException;




}
