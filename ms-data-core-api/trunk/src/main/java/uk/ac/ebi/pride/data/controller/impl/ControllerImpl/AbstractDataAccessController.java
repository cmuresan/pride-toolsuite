package uk.ac.ebi.pride.data.controller.impl.ControllerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.data.utils.QuantCvTermReference;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.gui.prop.PropertyChangeHelper;
import uk.ac.ebi.pride.term.CvTermReference;

import java.beans.PropertyChangeEvent;
import java.util.*;

/**
 * AbstractDataAccessController provides an abstract implementation of DataAccessController.
 * This is solely based on getting the data directly from data source.
 * <p/>
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 12:22:24
 */
public abstract class AbstractDataAccessController extends PropertyChangeHelper implements DataAccessController {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataAccessController.class);

    private static final int NUMBER_OF_PROTEIN_TO_CHECK = 10;
    private static final int NUMBER_OF_PEPTIDE_TO_CHECK = 20;

    /**
     * Unique id to identify the data access controller
     */
    private String uid = null;
    /**
     * The name of the data source for displaying purpose
     */
    private String name = null;
    /**
     * The description of the data source for displaying purpose
     */
    private String description = null;
    /**
     * The I/O type of the data source
     */
    private Type type = null;
    /**
     * The type of contents can be present in the data source
     */
    private Set<ContentCategory> categories = null;
    /**
     * Data source, such as: File
     */
    private Object source = null;
    /**
     * Foreground experiment accession, the one which user is currently viewing/analysing
     */
    private Comparable foregroundExperimentAcc = null;
    /**
     * Foreground spectrum, the one which user currently selected
     */
    private Spectrum foregroundSpectrum = null;
    /**
     * Foreground chromatogram, the one which user currently selected
     */
    private Chromatogram foregroundChromatogram = null;
    /**
     * Foreground protein identification, the one which user currently selected
     */
    private Protein foregroundProtein = null;


    /**
     * Create a data access controller without source
     */
    protected AbstractDataAccessController() {
        this(null);
    }

    /**
     * Create a data access controller with source
     *
     * @param source data source
     */
    protected AbstractDataAccessController(Object source) {
        setSource(source);
        // initialize content categories
        categories = new HashSet<ContentCategory>();
    }

    /**
     * Get the unique id of the data access controller
     *
     * @return String  unique id
     */
    @Override
    public String getUid() {
        return uid;
    }

    /**
     * Set the unique id of the data access controller
     *
     * @param uid unique id
     */
    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Get the name of the data access controller
     *
     * @return String  name of the data access controller
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the name of the data access controller
     *
     * @param name the new name for this DataAccessController
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the I/O type of the data access controller
     *
     * @return Type    I/O type
     */
    @Override
    public Type getType() {
        return type;
    }

    /**
     * Set the I/O type of the data access controller
     *
     * @param type controller type.
     */
    @Override
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Get the content could be present in the data access controller
     *
     * @return Collection<ContentCateogry> a list of content types
     */
    @Override
    public Collection<ContentCategory> getContentCategories() {
        return new ArrayList<ContentCategory>(categories);
    }

    /**
     * Set the content categories
     *
     * @param categories a array of categories.
     */
    @Override
    public void setContentCategories(ContentCategory... categories) {
        this.categories.clear();
        this.categories.addAll(Arrays.asList(categories));
    }

    /**
     * Get the description of the data access controller
     *
     * @return String  description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the data access controller
     *
     * @param desc the new description for the controller
     */
    @Override
    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     * Get the source of the data access controller
     *
     * @return Object  the source of the data access controller
     */
    @Override
    public Object getSource() {
        return source;
    }

    /**
     * Set the source of the data access controller
     *
     * @param src data source object
     */
    @Override
    public void setSource(Object src) {
        this.source = src;
    }

    /**
     * Close the data access controller
     */
    @Override
    public void close() {
        removeAllPropertyChangeListeners();
        firePropertyChange(DATA_SOURCE_CLOSED, false, true);
    }

    /**
     * Check spectrum availability
     *
     * @return boolean true means there is spectra
     * @throws DataAccessException data access exception
     */
    @Override
    public ExperimentMetaData getExperimentMetaData() throws DataAccessException {
        return null;
    }

    @Override
    public IdentificationMetaData getIdentificationMetaData() throws DataAccessException {
        return null;
    }

    @Override
    public MzGraphMetaData getMzGraphMetaData() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<CVLookup> getCvLookups() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public ParamGroup getFileContent() throws DataAccessException {
        return null;
    }

    @Override
    public ReferenceableParamGroup getReferenceableParamGroup() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Sample> getSamples() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<SpectrumIdentificationProtocol> getSpectrumIdentificationProtocol() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Protocol getProteinDetectionProtocol() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<SpectraData> getSpectraDataFiles() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Provider getProvider() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Software> getSoftwares() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<ScanSetting> getScanSettings() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<Person> getPersonContacts() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<Organization> getOrganizationContacts() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<SourceFile> getSourceFiles() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<DataProcessing> getDataProcessings() throws DataAccessException {
        return Collections.emptyList();
    }

    /**
     * Check spectrum availability
     *
     * @return boolean true means there is spectra
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasSpectrum() throws DataAccessException {
        return getNumberOfSpectra() > 0;
    }

    /**
     * Get the number of spectra
     *
     * @return int the number of spectra
     * @throws DataAccessException data access exception
     */
    @Override
    public int getNumberOfSpectra() throws DataAccessException {
        return getSpectrumIds().size();
    }

    /**
     * Get the index of the spectrum
     *
     * @param id spectrum id
     * @return int the index of the spectrum
     * @throws DataAccessException data access exception
     */
    @Override
    public int getSpectrumIndex(Comparable id) throws DataAccessException {
        int index = -1;
        Collection<Comparable> ids = getSpectrumIds();
        if (ids != null) {
            index = CollectionUtils.getIndex(ids, id);
        }
        return index;
    }

    /**
     * Get a collection spectrum starting from a given index
     *
     * @param index  the start index of the spectrum.
     * @param offset the max number of spectra to get.
     * @return Collection<Spectrum>    a collection of spectrum
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<Spectrum> getSpectraByIndex(int index, int offset) throws DataAccessException {
        List<Spectrum> spectra = new ArrayList<Spectrum>();
        Collection<Comparable> specIds = getSpectrumIds();
        if (specIds != null && index < specIds.size()) {
            int stopIndex = index + offset;
            int idSize = specIds.size();
            stopIndex = stopIndex >= idSize ? idSize : stopIndex;
            for (int i = index; i < stopIndex; i++) {
                Comparable specId = CollectionUtils.getElement(specIds, i);
                spectra.add(getSpectrumById(specId));
            }
        }

        return spectra;
    }

    /**
     * Return false by default
     *
     * @param specId spectrum id
     * @return boolean  true means a peptide has been identified
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean isIdentifiedSpectrum(Comparable specId) throws DataAccessException {
        return false;
    }

    /**
     * Get number of peaks using spectrum id.
     *
     * @param specId spectrum id.
     * @return int number of peaks
     * @throws DataAccessException data access exception
     */
    @Override
    public int getNumberOfPeaks(Comparable specId) throws DataAccessException {
        int numOfPeaks = 0;
        Spectrum spectrum = getSpectrumById(specId);
        if (spectrum != null) {
            numOfPeaks = DataAccessUtilities.getNumberOfPeaks(spectrum);
        }
        return numOfPeaks;
    }

    /**
     * Get ms level using spectrum id.
     *
     * @param specId spectrum id.
     * @return int ms level
     * @throws DataAccessException data access exception
     */
    @Override
    public int getMsLevel(Comparable specId) throws DataAccessException {
        int msLevel = -1;
        Spectrum spectrum = getSpectrumById(specId);
        if (spectrum != null) {
            msLevel = DataAccessUtilities.getMsLevel(spectrum);
        }
        return msLevel;
    }

    /**
     * Get precursor charge of a spectrum.
     *
     * @param specId spectrum id.
     * @return int precursor charge
     * @throws DataAccessException data access exception
     */
    @Override
    public int getPrecursorCharge(Comparable specId) throws DataAccessException {
        int charge = -1;
        Spectrum spectrum = getSpectrumById(specId);
        if (spectrum != null) {
            charge = DataAccessUtilities.getPrecursorCharge(spectrum);
        }
        return charge;
    }

    /**
     * Get precursor charge on peptide level
     * Note: sometimes, precursor charge at the peptide level is different from the precursor charge at the spectrum level
     * As the peptide-level precursor charge is often assigned by search engine rather than ms instrument
     *
     * @param proteinId   protein identification id
     * @param peptideId peptid eid, can be the index of the peptide as well.
     * @return precursor charge, 0 should be returned if not available
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          data access exception
     */
    @Override
    public int getPrecursorCharge(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        int charge = -1;

        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                charge = peptide.getPrecursorCharge();
            }
        }

        return charge;
    }

    /**
     * Get precursor m/z value using spectrum id.
     *
     * @param specId spectrum id.
     * @return double m/z
     * @throws DataAccessException data access exception
     */
    @Override
    public double getPrecursorMz(Comparable specId) throws DataAccessException {
        double mz = -1;
        Spectrum spectrum = getSpectrumById(specId);
        if (spectrum != null) {
            mz = DataAccessUtilities.getPrecursorMz(spectrum);
        }
        return mz;
    }

    /**
     * Get precursor m/z value at the peptide level
     *
     * @param proteinId   identification id
     * @param peptideId peptid eid, can be the index of the peptide as well.
     * @return precursor mass
     * @throws DataAccessException
     */
    @Override
    public double getPrecursorMz(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        double mz = -1;

        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                mz = peptide.getPrecursorMz();
            }
        }

        return mz;
    }

    /**
     * Get precursor intensity value using spectrum id.
     *
     * @param specId spectrum id.
     * @return double intensity
     * @throws DataAccessException data access exception
     */
    @Override
    public double getPrecursorIntensity(Comparable specId) throws DataAccessException {
        double intent = -1;
        Spectrum spectrum = getSpectrumById(specId);
        if (spectrum != null) {
            intent = DataAccessUtilities.getPrecursorIntensity(spectrum);
        }
        return intent;
    }

    /**
     * Get sum of intensity value using spectrum id.
     *
     * @param specId spectrum id.
     * @return double sum of intensity
     * @throws DataAccessException data access exception
     */
    @Override
    public double getSumOfIntensity(Comparable specId) throws DataAccessException {
        double sum = 0;
        Spectrum spectrum = getSpectrumById(specId);
        if (spectrum != null) {
            sum = DataAccessUtilities.getSumOfIntensity(spectrum);
        }
        return sum;
    }

    /**
     * Check the availability of the chromatogram
     *
     * @return boolean true means chromatogram available
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasChromatogram() throws DataAccessException {
        return getNumberOfChromatograms() > 0;
    }

    /**
     * Get the number of chromatograms
     *
     * @return int the number of chromatograms
     * @throws DataAccessException data access exception
     */
    @Override
    public int getNumberOfChromatograms() throws DataAccessException {
        return getChromatogramIds().size();
    }

    /**
     * Get the index of a chromatogram by id
     *
     * @param chromaId chromatogram id
     * @return int the index of the the chromatogram
     * @throws DataAccessException data access exception
     */
    @Override
    public int getChromatogramIndex(Comparable chromaId) throws DataAccessException {
        int index = -1;
        Collection<Comparable> ids = getChromatogramIds();
        if (ids != null) {
            index = CollectionUtils.getIndex(ids, chromaId);
        }
        return index;
    }

    /**
     * Get a collection of chromatograms starting from a given index
     *
     * @param index  index of the starting chromatogram.
     * @param offset the number of chromatogram to get.
     * @return Collection<Chromatogram>    a collection of chromatograms
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<Chromatogram> getChromatogramByIndex(int index, int offset) throws DataAccessException {
        List<Chromatogram> chromas = new ArrayList<Chromatogram>();
        Collection<Comparable> chromatogramIds = getChromatogramIds();
        if (chromatogramIds != null && index < chromatogramIds.size()) {
            int stopIndex = index + offset;
            int idSize = chromatogramIds.size();
            stopIndex = stopIndex >= idSize ? idSize : stopIndex;
            for (int i = index; i < stopIndex; i++) {
                Comparable chromaId = CollectionUtils.getElement(chromatogramIds, i);
                chromas.add(getChromatogramById(chromaId));
            }
        }

        return chromas;
    }

    /**
     * Check the availability of protein identifications
     *
     * @return boolean true means there is protein identifications
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasProtein() throws DataAccessException {
        return getNumberOfProteins() > 0;
    }

    /**
     * Check the availability of protein group identifications
     *
     * @return boolean true means there is protein identifications
     * @throws DataAccessException data access exception
     */

    @Override
    public boolean hasProteinGroup() throws DataAccessException {
        return getNumberOfProteins() > 0;
    }


    /**
     * Check the availability of peptide identifications
     *
     * @return boolean true means there is peptide identificaitons
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasPeptide() throws DataAccessException {
        return getNumberOfProteins() > 0;
    }

    /**
     * Get the number of protein identifications
     *
     * @return int the number of protein identifications
     * @throws DataAccessException data access exception
     */
    @Override
    public int getNumberOfProteins() throws DataAccessException {
        return getProteinIds().size();
    }


    /**
     * Get the index of a identification using its id
     *
     * @param proteinId identification id
     * @return int index of the identification
     * @throws DataAccessException
     */
    @Override
    public int indexOfProtein(Comparable proteinId) throws DataAccessException {
        int index = -1;
        Collection<Comparable> ids = getProteinIds();
        if (ids != null) {
            index = CollectionUtils.getIndex(ids, proteinId);
        }
        return index;
    }


    /**
     * Get a collection of protein identification starting from a given index
     *
     * @param index  starting index of the protein identification
     * @param offset number of identification to get.
     * @return Collection<Identification>  a collection of protein identifications
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<Protein> getProteinByIndex(int index, int offset) throws DataAccessException {
        List<Protein> proteins = new ArrayList<Protein>();
        Collection<Comparable> proteinIds = getProteinIds();
        if (proteinIds != null && index < proteinIds.size()) {
            int stopIndex = index + offset;
            int idSize = proteinIds.size();
            stopIndex = stopIndex >= idSize ? idSize : stopIndex;
            for (int i = index; i < stopIndex; i++) {
                Comparable intentId = CollectionUtils.getElement(proteinIds, i);
                proteins.add(getProteinById(intentId));
            }
        }

        return proteins;
    }

    /**
     * Get protein accession value using identification id.
     *
     * @param proteinId identification id.
     * @return String protein accession
     * @throws DataAccessException data access exception
     */
    @Override
    public String getProteinAccession(Comparable proteinId) throws DataAccessException {
        String acc = null;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            acc = protein.getDbSequence().getAccessionId();
        }
        return acc;
    }

    /**
     * Get protein accession version using identification id.
     *
     * @param proteinId identification id.
     * @return String
     * @throws DataAccessException
     */
    @Override
    public String getProteinAccessionVersion(Comparable proteinId) throws DataAccessException {
        String accVersion = null;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            accVersion = protein.getDbSequence().getAccessionVersion();
        }
        return accVersion;
    }

    /**
     * Get the type of the protein identification
     *
     * @param proteinId identification id.
     * @return String  the type of the protein identification
     * @throws DataAccessException data access exception
     */
    @Override
    public String getProteinType(Comparable proteinId) throws DataAccessException {
        String type = null;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            type = (protein.getGel() != null) ? TWO_DIM_PROTEIN_IDENTIFICATION_TYPE : GEL_FREE_PROTEIN_IDENTIFICATION_TYPE;
        }
        return type;
    }

    /**
     * Get identification score using identification id.
     *
     * @param proteinId identification id.
     * @return double identification score
     * @throws DataAccessException data access exception
     */
    @Override
    public double getProteinScore(Comparable proteinId) throws DataAccessException {
        double score = -1;
        Protein protein = getProteinById(proteinId);
        if ((protein != null) && (protein.getScore() != null)) {
            score = protein.getScore().getDefaultScore();
        }
        return score;
    }

    @Override
    public DBSequence getProteinSequence(Comparable proteinId) throws DataAccessException {
        DBSequence dbSequence = null;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            dbSequence = protein.getDbSequence();
        }
        return dbSequence;
    }

    /**
     * Get identification threshold using identification id.
     *
     * @param proteinId identification id.
     * @return double sum of intensity
     * @throws DataAccessException data access exception
     */
    @Override
    public double getProteinThreshold(Comparable proteinId) throws DataAccessException {
        double threshold = -1;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            threshold = protein.getThreshold();
        }
        return threshold;
    }

    /**
     * Get search database using identification id
     *
     * @param proteinId identification id.
     * @return String search database
     * @throws DataAccessException data accession exception
     */
    @Override
    public SearchDataBase getSearchDatabase(Comparable proteinId) throws DataAccessException {
        SearchDataBase database = null;
        Protein protein = getProteinById(proteinId);
        if (protein != null && (protein.getDbSequence() != null)) {
            database = protein.getDbSequence().getSearchDataBase();
        }
        return database;
    }

    /**
     * Get search database version for a given protein identification
     *
     * @param proteinId identification id
     * @return String  search database version
     * @throws DataAccessException data access exception
     */
    @Override
    public String getSearchDatabaseVersion(Comparable proteinId) throws DataAccessException {
        String version = null;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            version = protein.getDbSequence().getSearchDataBase().getVersion();
        }
        return version;
    }

    /**
     * Get search engine has been used.
     *
     * @return SearchEngine    search engine
     * @throws DataAccessException data access exception
     */
    @Override
    public SearchEngine getSearchEngine() throws DataAccessException {
        SearchEngine searchEngine = null;
        Collection<Comparable> proteinIds = this.getProteinIds();
        if (proteinIds.size() > 0) {
            Protein protein = getProteinById(CollectionUtils.getElement(proteinIds, 0));
            if (protein != null) {
                List<SearchEngineType> engines = (protein.getScore() == null) ? null : protein.getScore().getSearchEngineTypes();
                searchEngine = new SearchEngine(null, null, engines);
                // check the search engine types from the data source
                List<Peptide> peptides = protein.getPeptides();
                if (peptides != null && !peptides.isEmpty()) {
                    Peptide peptide = peptides.get(0);
                    List<SearchEngineType> types = DataAccessUtilities.getSearchEngineTypes(peptide.getSpectrumIdentification());
                    searchEngine.setSearchEngineTypes(types);
                }
            }
        }

        return searchEngine;
    }

    @Override
    public List<CvTermReference> getProteinCvTermReferenceScores() throws DataAccessException {
        Collection<Comparable> proteinIds = this.getProteinIds();
        if (proteinIds.size() > 0) {
            Protein protein = getProteinById(CollectionUtils.getElement(proteinIds, 0));
            if (protein != null) {
                Score score = DataAccessUtilities.getScore(protein);
                return score.getCvTermReferenceWithValues();
            }
        }
        return null;

    }

    @Override
    public List<CvTermReference> getPeptideCvTermReferenceScores() throws DataAccessException {
        Collection<Comparable> proteinIds = this.getProteinIds();
        if (proteinIds.size() > 0) {
            Protein protein = getProteinById(CollectionUtils.getElement(proteinIds, 0));
            if (protein != null) {
                List<Peptide> peptides = protein.getPeptides();
                Peptide peptide = peptides.get(0);
                Score score = DataAccessUtilities.getScore(peptide.getSpectrumIdentification());
                return score.getCvTermReferenceWithValues();
            }
        }
        return null;
    }

    @Override
    public Score getProteinScores(Comparable proteinId) throws DataAccessException {
        Protein protein = getProteinById(proteinId);
        Score score = null;
        if (protein != null) {
            score = DataAccessUtilities.getScore(protein);
            protein.setScore(score);
        }
        return score;
    }

    /**
     * Get peptide ids using identification id.
     *
     * @param proteinId identification id.
     * @return Collection<Comparable>   peptide ids collection
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Comparable> getPeptideIds(Comparable proteinId) throws DataAccessException {
        Collection<Comparable> ids = new ArrayList<Comparable>();
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            List<Peptide> peptides = protein.getPeptides();
            if (peptides != null) {
                for (int index = 0; index < peptides.size(); index++) {
                    ids.add(index);
                }
            }
        }
        return ids;
    }

    /**
     * Get peptide using its index in a protein identification
     *
     * @param proteinId protein identification id
     * @param index   peptide index
     * @return Peptide peptide identification
     * @throws DataAccessException data access exception
     */
    @Override
    public Peptide getPeptideByIndex(Comparable proteinId, Comparable index) throws DataAccessException {
        Peptide peptide = null;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(index.toString()));
        }
        return peptide;
    }


    /**
     * Get peptide sequences using identification id.
     *
     * @param proteinId identification id.
     * @return Collection<Comparable>   peptide ids collection
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPeptideSequences(Comparable proteinId) throws DataAccessException {
        List<String> sequences = new ArrayList<String>();
        // read from data source
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            List<Peptide> peptides = protein.getPeptides();
            if (peptides != null) {
                for (Peptide peptide : peptides) {
                    String seq = peptide.getPeptideSequence().getSequence();
                    sequences.add(seq);
                }
            }
        }

        return sequences;
    }

    @Override
    public Collection<PeptideEvidence> getPeptideEvidences(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        Peptide peptide = getPeptideByIndex(proteinId, peptideId);
        return peptide.getPeptideEvidenceList();
    }

    /**
     * Get number of peptides using identification id.
     *
     * @param proteinId identification id.
     * @return int   number of peptides
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfPeptides(Comparable proteinId) throws DataAccessException {
        int cnt = 0;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            cnt = DataAccessUtilities.getNumberOfPeptides(protein);
        }
        return cnt;
    }

    /**
     * Get the total number of peptides
     *
     * @return int  the total number of peptides.
     * @throws DataAccessException data access exception.
     */
    public int getNumberOfPeptides() throws DataAccessException {
        int cnt = 0;

        Collection<Comparable> ids = getProteinIds();
        if (ids != null) {
            for (Comparable id : ids) {
                cnt += getNumberOfPeptides(id);
            }
        }

        return cnt;
    }

    /**
     * Get number of unique peptides using identification id.
     *
     * @param proteinId identification id.
     * @return int   number of unique peptides
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfUniquePeptides(Comparable proteinId) throws DataAccessException {
        int cnt = 0;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            cnt = DataAccessUtilities.getNumberOfUniquePeptides(protein);
        }
        return cnt;
    }

    /**
     * Get number of ptms using identification id.
     *
     * @param proteinId identification id.
     * @return int   number of ptms
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfPTMs(Comparable proteinId) throws DataAccessException {
        int cnt = 0;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            cnt = DataAccessUtilities.getNumberOfPTMs(protein);
        }
        return cnt;
    }

    /**
     * Get number of ptms using peptide id.
     *
     * @param proteinId identification id.
     * @return int   number of ptms
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfPTMs(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        int cnt = 0;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            List<Peptide> peptides = protein.getPeptides();
            if (peptides != null) {
                Peptide peptide = peptides.get(Integer.parseInt(peptideId.toString()));
                if (peptide != null) {
                    cnt = DataAccessUtilities.getNumberOfPTMs(peptide);
                }
            }
        }
        return cnt;
    }

    @Override
    public Collection<Modification> getModification() throws DataAccessException {
        return Collections.emptyList();
        //Todo: Think About how we can report all of the modifications reported in the file.
    }

    @Override
    public Collection<SearchDataBase> getSearchDataBases() throws DataAccessException {
        return Collections.emptyList();
        //Todo: Think About how we can report all the search databases reported in the file.
    }

    /**
     * Get peptide sequence using identification id and peptide id.
     *
     * @param proteinId identification id.
     * @return int   number of unique peptides
     * @throws DataAccessException data access exception
     */
    @Override
    public String getPeptideSequence(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        String seq = null;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                seq = peptide.getPeptideSequence().getSequence();
            }
        }
        return seq;
    }

    /**
     * Get peptide sequence start using identification id and peptide id.
     *
     * @param proteinId identification id.
     * @return int   peptide sequence start
     * @throws DataAccessException data access exception
     */
    @Override
    public int getPeptideSequenceStart(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        int start = -1;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                start = peptide.getPeptideEvidenceList().get(0).getStartPosition();
                //Todo: We need to define finally how to manage the information for pride xml object as PeptideEvidence
            }
        }
        return start;
    }

    /**
     * Get peptide sequence stop using identification id and peptide id.
     *
     * @param proteinId identification id.
     * @return int   peptide sequence stop
     * @throws DataAccessException data access exception
     */
    @Override
    public int getPeptideSequenceEnd(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        int stop = -1;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                stop = peptide.getPeptideEvidenceList().get(0).getEndPosition();
            }
        }
        return stop;
    }

    /**
     * Get peptide spectrum id using identification id and peptide id.
     *
     * @param proteinId identification id.
     * @return int   peptide sequence stop
     * @throws DataAccessException data access exception
     */
    @Override
    public Comparable getPeptideSpectrumId(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        Comparable specId = null;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                Spectrum spectrum = peptide.getSpectrum();
                if (spectrum != null) {
                    specId = spectrum.getId();
                }
            }
        }
        return specId;
    }

    /**
     * Get ptms using identification id nad peptide id
     *
     * @param proteinId   identification id
     * @param peptideId peptide id, can be the index of the peptide
     * @return List<Modification>   a list of modifications.
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Modification> getPTMs(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        List<Modification> mods = new ArrayList<Modification>();
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                List<Modification> rawMods = peptide.getPeptideSequence().getModificationList();
                mods.addAll(rawMods);
            }
        }
        return mods;
    }

    @Override
    public Collection<SubstitutionModification> getSubstitutionPTMs(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        List<SubstitutionModification> mods = new ArrayList<SubstitutionModification>();
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                List<SubstitutionModification> rawMods = peptide.getPeptideSequence().getSubstitutionModificationList();
                mods.addAll(rawMods);
            }
        }
        return mods;
    }

    @Override
    public int getNumberOfSubstitutionPTMs(Comparable proteinId) throws DataAccessException {
        int cnt = 0;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            cnt = DataAccessUtilities.getNumberOfSubstitutionPTMs(protein);
        }
        return cnt;
    }

    @Override
    public int getNumberOfSubstitutionPTMs(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        int cnt = 0;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            List<Peptide> peptides = protein.getPeptides();
            if (peptides != null) {
                Peptide peptide = peptides.get(Integer.parseInt(peptideId.toString()));
                if (peptide != null) {
                    cnt = DataAccessUtilities.getNumberOfSubstitutionPTMs(peptide);
                }
            }
        }
        return cnt;
    }

    /**
     * Get the number of fragment ions of a given peptide
     *
     * @param proteinId   identification id
     * @param peptideId peptide id, can be the index of the peptide as well.
     * @return int the number of fragment ions
     * @throws DataAccessException data access exception
     */
    @Override
    public int getNumberOfFragmentIons(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        int num = 0;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                List<FragmentIon> ions = peptide.getFragmentation();
                if (ions != null) {
                    num = ions.size();
                }
            }
        }
        return num;
    }

    /**
     * Get all the fragment ions of a given peptide identification
     *
     * @param proteinId   identification id
     * @param peptideId peptide id, can be the index of the peptide as well.
     * @return Collection<FragmentIon> a collection of fragment ions
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<FragmentIon> getFragmentIons(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        List<FragmentIon> frags = new ArrayList<FragmentIon>();
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                List<FragmentIon> rawFrags = peptide.getFragmentation();
                frags.addAll(rawFrags);
            }
        }
        return frags;
    }

    /**
     * Get the search score for a given peptide
     *
     * @param proteinId   identification id
     * @param peptideId peptide id, can be the index of the peptide as well.
     * @return PeptideScore    search engine score
     * @throws DataAccessException data access exception
     */
    @Override
    public Score getPeptideScore(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        Score score = null;
        Protein protein = getProteinById(proteinId);
        if (protein != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(protein, Integer.parseInt(peptideId.toString()));
            if ((peptide != null) || (peptide.getSpectrumIdentification().getScore() == null)) {
                score = DataAccessUtilities.getScore(peptide.getSpectrumIdentification());
                peptide.getSpectrumIdentification().setScore(score);
            }
        }
        return score;
    }

    /**
     * Get foreground experiment accession
     *
     * @return Comparable  experiment accession
     */
    @Override
    public synchronized Comparable getForegroundExperimentAcc() {
        return foregroundExperimentAcc;
    }

    /**
     * Set foreground experiment accession
     *
     * @param expAcc new experiment accession
     * @throws DataAccessException data access exception
     */
    @Override
    public void setForegroundExperimentAcc(Comparable expAcc) throws DataAccessException {
        logger.debug("Set foreground experiment accession: {}", expAcc);
        Comparable acc = foregroundExperimentAcc;
        if (!expAcc.equals(acc)) {
            Comparable oldExp, newExp;
            synchronized (this) {
                oldExp = foregroundExperimentAcc;
                foregroundExperimentAcc = expAcc;
                newExp = foregroundExperimentAcc;
            }
            firePropertyChange(FOREGROUND_EXPERIMENT_CHANGED, oldExp, newExp);
        }
    }

    /**
     * Get foreground chromatogram
     *
     * @return Chromatogram    foreground chromatogram
     */
    @Override
    public synchronized Chromatogram getForegroundChromatogram() {
        return foregroundChromatogram;
    }

    /**
     * Set foreground chromatogram
     *
     * @param chromaId chromatogram id
     * @throws DataAccessException data access exception
     */
    @Override
    public void setForegroundChromatogramById(Comparable chromaId) throws DataAccessException {
        logger.debug("Set foreground chromatogram id: {}", chromaId);

        Chromatogram oldChroma = null;
        Chromatogram newChroma = this.getChromatogramById(chromaId);
        synchronized (this) {
            if (foregroundChromatogram == null ||
                    (newChroma != null && !foregroundChromatogram.getId().equals(newChroma.getId()))) {
                oldChroma = foregroundChromatogram;
                foregroundChromatogram = newChroma;
            }
        }
        firePropertyChange(FOREGROUND_CHROMATOGRAM_CHANGED, oldChroma, newChroma);
    }

    /**
     * get foreground spectrum
     *
     * @return spectrum    foreground spectrum
     */
    @Override
    public synchronized Spectrum getForegroundSpectrum() {
        return foregroundSpectrum;
    }

    /**
     * Set foreground spectrum using a given id
     *
     * @param specId spectrum id
     * @throws DataAccessException data access exception
     */
    @Override
    public void setForegroundSpectrumById(Comparable specId) throws DataAccessException {
        logger.debug("Set foreground spectrum id: {}", specId);

        Spectrum oldSpec = null;
        Spectrum newSpec = this.getSpectrumById(specId);
        synchronized (this) {
            if (foregroundSpectrum == null ||
                    (newSpec != null && !foregroundSpectrum.getId().equals(newSpec.getId()))) {
                oldSpec = foregroundSpectrum;
                foregroundSpectrum = this.getSpectrumById(specId);
            }
        }
        firePropertyChange(FOREGROUND_SPECTRUM_CHANGED, oldSpec, newSpec);
    }

    /**
     * Get the foreground mzgraph, can be either chromatogram or spectrum, depends on the given class type
     *
     * @param classType classes that extends MzGraph
     * @return MzGraph foreground mzgrap object
     */
    @Override
    public MzGraph getForegroundMzGraph(Class<? extends MzGraph> classType) {
        MzGraph mzGraph = null;

        if (classType.equals(Spectrum.class)) {
            mzGraph = getForegroundSpectrum();
        } else if (classType.equals(Chromatogram.class)) {
            mzGraph = getForegroundChromatogram();
        }

        return mzGraph;
    }

    /**
     * Get foreground protein identification
     *
     * @return Identification  protein identification
     */
    @Override
    public synchronized Protein getForegroundProtein() {
        return foregroundProtein;
    }

    /**
     * Set foreground protein identification using a given id
     *
     * @param proteinId identification id
     * @throws DataAccessException data access exception
     */
    @Override
    public void setForegroundIdentificationById(Comparable proteinId) throws DataAccessException {
        logger.debug("Set foreground identification id: {}", proteinId);

        Protein oldProtein = null;
        Protein newProtein = this.getProteinById(proteinId);
        synchronized (this) {
            if (foregroundProtein == null ||
                    (newProtein != null && !foregroundProtein.getId().equals(newProtein.getId()))) {
                oldProtein = foregroundProtein;
                foregroundProtein = getProteinById(proteinId);
            }
        }
        firePropertyChange(FOREGROUND_IDENTIFICATION_CHANGED, oldProtein, newProtein);
    }

    /**
     * Check the availability of quantitative data
     *
     * @return boolean true mean there is quantitative data
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasQuantData() throws DataAccessException {
        Collection<QuantCvTermReference> methods = getQuantMethods();
        return methods.size() > 0;
    }

    /**
     * Check the availability of quantitative data at the protein identification level
     *
     * @return boolean true means there is protein quantitative data
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasProteinQuantData() throws DataAccessException {
        Collection<QuantCvTermReference> methods = getQuantMethods();

        for (QuantCvTermReference method : methods) {
            if (QuantCvTermReference.containsProteinQuantification(method)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check the availability of quantitative data at the peptide identification level
     *
     * @return boolean true means there is peptide quantitative data
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasPeptideQuantData() throws DataAccessException {
        Collection<QuantCvTermReference> methods = getQuantMethods();

        for (QuantCvTermReference method : methods) {
            if (QuantCvTermReference.containsPeptideQuantification(method)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check the availability of total intensities at the protein identification level
     *
     * @return boolean true means there is total intensities
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasProteinTotalIntensities() throws DataAccessException {
        return getProteinQuantUnit() == null;
    }

    /**
     * Check the availability of total intensities at the peptide identification level
     *
     * @return boolean true means there is total intensities
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasPeptideTotalIntensities() throws DataAccessException {
        return getPeptideQuantUnit() == null;
    }

    /**
     * Check whether label free methods have been used
     *
     * @return boolean true means there are label free methods
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasLabelFreeQuantMethods() throws DataAccessException {
        // get the samples
        ParamGroup additionals = getAdditional();

        if (additionals != null) {
            // iterate over each sample
            List<CvParam> cvParams = additionals.getCvParams();
            for (CvParam cvParam : cvParams) {
                if (QuantCvTermReference.isLabelFreeMethod(cvParam)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check whether isotope labelling methods have been used
     *
     * @return boolean true means isotope labelling methods present
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean hasIsotopeLabellingQuantMethods() throws DataAccessException {
        // get the samples
        ParamGroup additionals = getAdditional();

        if (additionals != null) {
            // iterate over each sample
            List<CvParam> cvParams = additionals.getCvParams();
            if (cvParams != null) {
                for (CvParam cvParam : cvParams) {
                    if (QuantCvTermReference.isIsotopeLabellingMethodParam(cvParam)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * get all the quantitative methods used
     *
     * @return Collection<QuantCvTermReference>    a collection of quantitative methods
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<QuantCvTermReference> getQuantMethods() throws DataAccessException {
        Set<QuantCvTermReference> methods = new LinkedHashSet<QuantCvTermReference>();

        // get the samples
        ParamGroup additionals = getAdditional();

        if (additionals != null) {
            // iterate over each sample
            List<CvParam> cvParams = additionals.getCvParams();
            if (cvParams != null) {
                for (CvParam cvParam : cvParams) {
                    if (QuantCvTermReference.isQuantitativeMethodParam(cvParam)) {
                        methods.add(QuantCvTermReference.getQuantitativeMethodParam(cvParam));
                    }
                }
            }
        }
        return methods;
    }

    /**
     * Get a collection of all the label free methods used
     *
     * @return Collection<QuantCvTermReference>    a collection of label free methods
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<QuantCvTermReference> getLabelFreeQuantMethods() throws DataAccessException {
        Set<QuantCvTermReference> methods = new LinkedHashSet<QuantCvTermReference>();

        // get the samples
        ParamGroup additionals = getAdditional();

        if (additionals != null) {
            // iterate over each sample
            List<CvParam> cvParams = additionals.getCvParams();
            if (cvParams != null) {
                for (CvParam cvParam : cvParams) {
                    if (QuantCvTermReference.isLabelFreeMethod(cvParam)) {
                        methods.add(QuantCvTermReference.getQuantitativeMethodParam(cvParam));
                    }
                }
            }

        }

        return methods;
    }

    /**
     * Get all the label free methods used at the protein identification level
     *
     * @return Collection<QuantCvTermReference>    a collection of label free methods
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<QuantCvTermReference> getProteinLabelFreeQuantMethods() throws DataAccessException {
        Collection<QuantCvTermReference> methods = getLabelFreeQuantMethods();
        Collection<QuantCvTermReference> protMethods = new ArrayList<QuantCvTermReference>();

        for (QuantCvTermReference method : methods) {
            if (QuantCvTermReference.containsProteinQuantification(method)) {
                protMethods.add(method);
            }
        }

        return protMethods;
    }

    /**
     * Get all the label free methods used at the peptide identification level
     *
     * @return Collection<QuantCvTermReference>    a collection of label free methods
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<QuantCvTermReference> getPeptideLabelFreeQuantMethods() throws DataAccessException {
        Collection<QuantCvTermReference> methods = getLabelFreeQuantMethods();
        Collection<QuantCvTermReference> peptideMethods = new ArrayList<QuantCvTermReference>();

        for (QuantCvTermReference method : methods) {
            if (QuantCvTermReference.containsPeptideQuantification(method)) {
                peptideMethods.add(method);
            }
        }

        return peptideMethods;
    }

    /**
     * Get all the isotope labelling methods used
     *
     * @return Collection<QuantCvTermReference>    a collection of isotope labelling methods
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<QuantCvTermReference> getIsotopeLabellingQuantMethods() throws DataAccessException {
        Set<QuantCvTermReference> methods = new LinkedHashSet<QuantCvTermReference>();

        // get the samples
        ParamGroup additionals = getAdditional();

        if (additionals != null) {
            // iterate over each sample
            List<CvParam> cvParams = additionals.getCvParams();
            if (cvParams != null) {
                for (CvParam cvParam : cvParams) {
                    if (QuantCvTermReference.isIsotopeLabellingMethodParam(cvParam)) {
                        methods.add(QuantCvTermReference.getQuantitativeMethodParam(cvParam));
                    }
                }
            }
        }

        return methods;
    }

    /**
     * Get protein identification level isotope labelling methods
     *
     * @return Collection<QuantCvTermReference>    a collection of isotope labelling methods
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<QuantCvTermReference> getProteinIsotopeLabellingQuantMethods() throws DataAccessException {
        Collection<QuantCvTermReference> methods = getIsotopeLabellingQuantMethods();
        Collection<QuantCvTermReference> protMethods = new ArrayList<QuantCvTermReference>();

        for (QuantCvTermReference method : methods) {
            if (QuantCvTermReference.containsProteinQuantification(method)) {
                protMethods.add(method);
            }
        }

        return protMethods;
    }

    /**
     * Get peptide identification level isotope labelling methods
     *
     * @return Collection<QuantCvTermReference>    a collection of isotope labelling methods
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<QuantCvTermReference> getPeptideIsotopeLabellingQuantMethods() throws DataAccessException {
        Collection<QuantCvTermReference> methods = getIsotopeLabellingQuantMethods();
        Collection<QuantCvTermReference> peptideMethods = new ArrayList<QuantCvTermReference>();

        for (QuantCvTermReference method : methods) {
            if (QuantCvTermReference.containsPeptideQuantification(method)) {
                peptideMethods.add(method);
            }
        }

        return peptideMethods;
    }

    /**
     * Get the number of reagents
     *
     * @return int the number of reagents
     * @throws DataAccessException data access exception
     */
    @Override
    public int getNumberOfReagents() throws DataAccessException {
        int num = 0;

        if (hasIsotopeLabellingQuantMethods()) {
            // get samples
            Collection<Sample> samples = getSamples();

            if (samples != null) {
                for (Sample sample : samples) {
                    List<CvParam> cvParams = sample.getCvParams();
                    for (CvParam cvParam : cvParams) {
                        if (QuantCvTermReference.isReagent(cvParam)) {
                            num++;
                        }
                    }
                }
            }
        }

        return num;
    }

    /**
     * Check the first ten identification/peptide to get the reference sub sample's index
     *
     * @return index   reference sub sample index
     * @throws DataAccessException
     */
    @Override
    public int getReferenceSubSampleIndex() throws DataAccessException {
        int index = -1;

        if (hasIsotopeLabellingQuantMethods()) {
            int cnt = NUMBER_OF_PROTEIN_TO_CHECK;
            if (hasProteinQuantData()) {
                Collection<Comparable> proteinIds = getProteinIds();

                for (Comparable proteinId : proteinIds) {
                    Quantitation quant = getProteinQuantData(proteinId);
                    if (quant.hasTotalIntensities()) {
                        return index;
                    } else {
                        index = quant.getReferenceSubSampleIndex();
                        if (index > 0) {
                            break;
                        }
                    }
                    cnt--;
                    if (cnt == 0) {
                        break;
                    }
                }
            } else if (hasPeptideQuantData()) {
                Collection<Comparable> proteinIds = getProteinIds();

                for (Comparable proteinId : proteinIds) {
                    Collection<Comparable> peptideIds = getPeptideIds(proteinId);
                    for (Comparable peptideId : peptideIds) {
                        Quantitation quant = getPeptideQuantData(proteinId, peptideId);
                        if (quant.hasTotalIntensities()) {
                            return index;
                        } else {
                            index = quant.getReferenceSubSampleIndex();
                            if (index > 0) {
                                break;
                            }
                        }
                    }
                    cnt--;
                    if (cnt == 0) {
                        break;
                    }
                }
            }
        }

        return index;
    }

    /**
     * Get quantitative sample details
     *
     * @return QuantitativeSample  quantitative sample
     * @throws DataAccessException data access exception
     */
    @Override
    public QuantitativeSample getQuantSample() throws DataAccessException {
        QuantitativeSample sampleDesc = new QuantitativeSample();

        Collection<Sample> samples = getSamples();
        if (samples != null && !samples.isEmpty()) {
            Sample sample = CollectionUtils.getElement(samples, 0);
            List<CvParam> cvParams = sample.getCvParams();
            // scan for all the species
            if (cvParams != null) {
                for (CvParam cvParam : cvParams) {
                    String cvLabel = cvParam.getCvLookupID().toLowerCase();
                    if ("newt".equals(cvLabel)) {
                        sampleDesc.setSpecies(cvParam);
                    } else if ("bto".equals(cvLabel)) {
                        sampleDesc.setTissue(cvParam);
                    } else if ("cl".equals(cvLabel)) {
                        sampleDesc.setCellLine(cvParam);
                    } else if ("go".equals(cvLabel)) {
                        sampleDesc.setGOTerm(cvParam);
                    } else if ("doid".equals(cvLabel)) {
                        sampleDesc.setDisease(cvParam);
                    } else if (QuantCvTermReference.isSubSampleDescription(cvParam)) {
                        sampleDesc.setDescription(cvParam);
                    } else if (QuantCvTermReference.isReagent(cvParam)) {
                        sampleDesc.setReagent(cvParam);
                    }
                }
            }
        }

        return sampleDesc;
    }

    /**
     * Get quantitative unit used at the protein identification level
     * <p/>
     * Note: this method will scan through first 10 identifications
     *
     * @return Quantitation Cv Terms
     * @throws DataAccessException Exception related with the retrieve of the data
     */
    @Override
    public QuantCvTermReference getProteinQuantUnit() throws DataAccessException {
        Collection<Comparable> proteinIds = getProteinIds();

        int cnt = NUMBER_OF_PROTEIN_TO_CHECK;

        for (Comparable proteinId : proteinIds) {
            Quantitation quant = getProteinQuantData(proteinId);
            QuantCvTermReference unit = quant.getUnit();
            cnt--;
            if (unit != null) {
                return unit;
            }

            if (cnt == 0) {
                break;
            }
        }
        return null;
    }

    /**
     * Get quantitative unit used at the peptide identification level
     * <p/>
     * Note: this method will scan all the peptides
     *
     * @return QuantCvTermReference    unit cv term
     * @throws DataAccessException data access exception
     */
    @Override
    public QuantCvTermReference getPeptideQuantUnit() throws DataAccessException {
        Collection<Comparable> proteinIds = getProteinIds();

        int cnt = NUMBER_OF_PEPTIDE_TO_CHECK;

        for (Comparable proteinId : proteinIds) {
            Collection<Comparable> peptideIds = getPeptideIds(proteinId);
            for (Comparable peptideId : peptideIds) {
                Quantitation quant = getPeptideQuantData(proteinId, peptideId);
                QuantCvTermReference unit = quant.getUnit();
                if (unit != null) {
                    return unit;
                }
            }
            cnt--;

            if (cnt == 0) {
                break;
            }
        }
        return null;
    }

    /**
     * Get protein level quantitative data using a given protein identification id
     *
     * @param proteinId protein identification id
     * @return Quantitation    quantitative data
     * @throws DataAccessException data access exception
     */
    @Override
    public Quantitation getProteinQuantData(Comparable proteinId) throws DataAccessException {
        Protein protein = getProteinById(proteinId);
        return new Quantitation(Quantitation.Type.PROTEIN, protein.getCvParams());
    }

    /**
     * Get peptide level quantitative data using a given peptide identification id
     *
     * @param proteinId   protein identification id
     * @param peptideId peptide id
     * @return Quantitation    quantitative data
     * @throws DataAccessException data access exception
     */
    @Override
    public Quantitation getPeptideQuantData(Comparable proteinId, Comparable peptideId) throws DataAccessException {
        Peptide peptide = getPeptideByIndex(proteinId, peptideId);
        return new Quantitation(Quantitation.Type.PEPTIDE, peptide.getSpectrumIdentification().getCvParams());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // empty method
    }

    /**
     * By default all the supported files contains Metadata. Only the Peak Files do not contain any Metadata.
     *
     * @return boolean
     */
    @Override
    public boolean hasMetaDataInformation() {
        return true;
    }
}
