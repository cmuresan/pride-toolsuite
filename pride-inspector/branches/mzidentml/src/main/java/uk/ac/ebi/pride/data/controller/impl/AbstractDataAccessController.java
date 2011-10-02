package uk.ac.ebi.pride.data.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.coreIdent.*;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.data.utils.QuantCvTermReference;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

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
public abstract class AbstractDataAccessController extends PropertyChangeHelper   implements DataAccessController {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataAccessController.class);

    private String uid = null;

    private String name = null;

    private String description = null;

    private Type type = null;

    private Set<ContentCategory> categories = null;

    private Object source = null;

    protected SearchEngine searchEngine = null;

    protected Comparable foregroundExperimentAcc = null;

    protected Spectrum foregroundSpectrum = null;

    protected Chromatogram foregroundChromatogram = null;

    protected Identification foregroundIdentification = null;

    protected AbstractDataAccessController() {
        this(null);
    }

    protected AbstractDataAccessController(Object source) {
        setSource(source);
        // initialize content categories
        categories = new HashSet<ContentCategory>();
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public Collection<ContentCategory> getContentCategories() {
        return new ArrayList<ContentCategory>(categories);
    }

    @Override
    public void setContentCategories(ContentCategory... categories) {
        this.categories.clear();
        this.categories.addAll(Arrays.asList(categories));
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String desc) {
        this.description = desc;
    }

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public void setSource(Object src) {
        this.source = src;
    }

    @Override
    public void close() {
        clearCache();
        removeAllPropertyChangeListeners();
        firePropertyChange(DATA_SOURCE_CLOSED, false, true);
    }

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
    public Provider getProvider() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Software> getSoftwareList() throws DataAccessException {
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

    @Override
    public boolean hasSpectrum() throws DataAccessException {
        return getNumberOfSpectra() > 0;
    }

    @Override
    public int getNumberOfSpectra() throws DataAccessException {
        return getSpectrumIds().size();
    }

    @Override
    public int getSpectrumIndex(Comparable id) throws DataAccessException {
        int index = -1;
        Collection<Comparable> ids = getSpectrumIds();
        if (ids != null) {
            index = CollectionUtils.getIndex(ids, id);
        }
        return index;
    }

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
     * @return
     * @throws DataAccessException
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
     * This implementation will check cache first.
     *
     * @param specId spectrum id.
     * @return int precursor charge
     * @throws DataAccessException data access exception
     */
    @Override
    public int getPrecursorCharge(Comparable specId) throws DataAccessException {
        int charge = 0;
        Spectrum spectrum = getSpectrumById(specId);
        if (spectrum != null) {
            charge = DataAccessUtilities.getPrecursorCharge(spectrum);
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
     * Get precursor intensity value using spectrum id.
     * This implementation will check cache first.
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

    @Override
    public boolean hasChromatogram() throws DataAccessException {
        return getNumberOfChromatograms() > 0;
    }

    @Override
    public int getNumberOfChromatograms() throws DataAccessException {
        return getChromatogramIds().size();
    }


    @Override
    public int getChromatogramIndex(Comparable id) throws DataAccessException {
        int index = -1;
        Collection<Comparable> ids = getChromatogramIds();
        if (ids != null) {
            index = CollectionUtils.getIndex(ids, id);
        }
        return index;
    }

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

    @Override
    public boolean hasIdentification() throws DataAccessException {
        return getNumberOfIdentifications() > 0;
    }

    @Override
    public boolean hasPeptide() throws DataAccessException {
        return getNumberOfIdentifications() > 0;
    }

    @Override
    public int getNumberOfIdentifications() throws DataAccessException {
        return getIdentificationIds().size();
    }

    @Override
    public int getIdentificationIndex(Comparable id) throws DataAccessException {
        int index = -1;
        Collection<Comparable> ids = getIdentificationIds();
        if (ids != null) {
            index = CollectionUtils.getIndex(ids, id);
        }
        return index;
    }


    @Override
    public Collection<Identification> getIdentificationsByIndex(int index, int offset) throws DataAccessException {
        List<Identification> idents = new ArrayList<Identification>();
        Collection<Comparable> identIds = getIdentificationIds();
        if (identIds != null && index < identIds.size()) {
            int stopIndex = index + offset;
            int idSize = identIds.size();
            stopIndex = stopIndex >= idSize ? idSize : stopIndex;
            for (int i = index; i < stopIndex; i++) {
                Comparable intentId = CollectionUtils.getElement(identIds, i);
                idents.add(getIdentificationById(intentId));
            }
        }

        return idents;
    }

    /**
     * Get protein accession value using identification id.
     *
     * @param identId identification id.
     * @return String protein accession
     * @throws DataAccessException data access exception
     */
    @Override
    public String getProteinAccession(Comparable identId) throws DataAccessException {
        String acc = null;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            acc = ident.getDbSequence().getAccessionId();
        }
        return acc;
    }

    /**
     * Get protein accession version using identification id.
     *
     * @param identId identification id.
     * @return String
     * @throws DataAccessException
     */
    @Override
    public String getProteinAccessionVersion(Comparable identId) throws DataAccessException {
        String accVersion = null;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            accVersion = ident.getDbSequence().getAccessionVersion();
        }
        return accVersion;
    }

    /**
     * Get the Type of Identification (Gel base Identification or Not Gel presented)
     * @param identId identification id.
     * @return
     * @throws DataAccessException
     */
    @Override
    public String getIdentificationType(Comparable identId) throws DataAccessException {
        String type = null;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            type = ident instanceof TwoDimIdentification ? TWO_DIM_IDENTIFICATION_TYPE : GEL_FREE_IDENTIFICATION_TYPE;
        }
        return type;
    }

    /**
     * Get identification score using identification id.
     *
     * @param identId identification id.
     * @return double identification score
     * @throws DataAccessException data access exception
     */
    @Override
    public double getIdentificationScore(Comparable identId) throws DataAccessException {
        double score = -1;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            score = ident.getScore();
        }
        return score;
    }

    @Override
    public DBSequence getIdentificationSequence(Comparable identId) throws DataAccessException {
        DBSequence dbSequence= null;
        Identification ident = getIdentificationById(identId);
        if(ident != null){
            dbSequence = ident.getDbSequence();
        }
        return dbSequence;
    }

    /**
     * Get identification threshold using identification id.
     *
     * @param identId identification id.
     * @return double sum of intensity
     * @throws DataAccessException data access exception
     */
    @Override
    public double getIdentificationThreshold(Comparable identId) throws DataAccessException {
        double threshold = -1;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            threshold = ident.getThreshold();
        }
        return threshold;
    }

    /**
     * Get search database using identfication id
     *
     * @param identId identification id.
     * @return String search database
     * @throws DataAccessException data accession exception
     */
    @Override
    public SearchDataBase getSearchDatabase(Comparable identId) throws DataAccessException {
        SearchDataBase database = null;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            database = ident.getDbSequence().getSearchDataBase();
        }
        return database;
    }

    /**
     * Get search engine has been used.
     *
     * @return  SearchEngine    search engine
     * @throws DataAccessException  data access exception
     */
    @Override
    public SearchEngine getSearchEngine() throws DataAccessException {
        if (searchEngine == null && hasIdentification()) {
            Collection<Comparable> identIds = this.getIdentificationIds();
            Identification ident = getIdentificationById(CollectionUtils.getElement(identIds, 0));
            if (ident != null) {
                searchEngine = new SearchEngine(ident.getSearchEngine());
                // check the search engine types from the data source
                List<Peptide> peptides = ident.getIdentifiedPeptides();
                Peptide peptide = peptides.get(0);
                List<SearchEngineType> types = DataAccessUtilities.getSearchEngineTypes(peptide);
                searchEngine.setSearchEngineTypes(types);
            }
        }

        return searchEngine;
    }

    /**
     * Get peptide ids using identification id.
     *
     * @param identId identification id.
     * @return Collection<Comparable>   peptide ids collection
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Comparable> getPeptideIds(Comparable identId) throws DataAccessException {
        Collection<Comparable> ids = new ArrayList<Comparable>();
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            List<Peptide> peptides = ident.getIdentifiedPeptides();
            if (peptides != null) {
                for (int index = 0; index < peptides.size(); index++) {
                    ids.add(index);
                }
            }
        }
        return ids;
    }

    @Override
    public Peptide getPeptideById(Comparable id, Comparable index) throws DataAccessException {
        Peptide peptide = null;
        Identification ident = getIdentificationById(id);
        if (ident != null) {
            peptide = DataAccessUtilities.getPeptide(ident, Integer.parseInt(index.toString()));
        }
        return peptide;
    }


    /**
     * Get peptide sequences using identification id.
     *
     * @param identId identification id.
     * @return Collection<Comparable>   peptide ids collection
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPeptideSequences(Comparable identId) throws DataAccessException {
        List<String> sequences = new ArrayList<String>();
        // read from data source
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            List<Peptide> peptides = ident.getIdentifiedPeptides();
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
    public Collection<PeptideEvidence> getPeptideEvidences(Comparable identId, Comparable peptideId) throws DataAccessException {
        Peptide peptide = getPeptideById(identId,peptideId);
        return  peptide.getPeptideEvidenceList();
    }

    /**
     * Get number of peptides using identification id.
     *
     * @param identId identification id.
     * @return int   number of peptides
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfPeptides(Comparable identId) throws DataAccessException {
        int cnt = 0;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            cnt = DataAccessUtilities.getNumberOfPeptides(ident);
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

        Collection<Comparable> ids = getIdentificationIds();
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
     * @param identId identification id.
     * @return int   number of unique peptides
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfUniquePeptides(Comparable identId) throws DataAccessException {
        int cnt = 0;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            cnt = DataAccessUtilities.getNumberOfUniquePeptides(ident);
        }
        return cnt;
    }

    /**
     * Get number of ptms using identification id.
     *
     * @param identId identification id.
     * @return int   number of ptms
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfPTMs(Comparable identId) throws DataAccessException {
        int cnt = 0;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            cnt = DataAccessUtilities.getNumberOfPTMs(ident);
        }
        return cnt;
    }

    /**
     * Get number of ptms using peptide id.
     *
     * @param identId identification id.
     * @return int   number of ptms
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfPTMs(Comparable identId, Comparable peptideId) throws DataAccessException {
        int cnt = 0;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            List<Peptide> peptides = ident.getIdentifiedPeptides();
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
        //Todo: Think About how we can report all of the modifciations reported in the file.
    }

    @Override
    public Collection<SearchDataBase> getSearchDataBase() throws DataAccessException {
        return Collections.emptyList();
        //Todo: Think About how we can report all the search databases reported in the file.
    }

    /**
     * Get peptide sequence using identification id and peptide id.
     *
     * @param identId identification id.
     * @return int   number of unique peptides
     * @throws DataAccessException data access exception
     */
    @Override
    public String getPeptideSequence(Comparable identId, Comparable peptideId) throws DataAccessException {
        String seq = null;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(ident, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                seq = peptide.getPeptideSequence().getSequence();
            }
        }
        return seq;
    }

    /**
     * Get peptide sequence start using identification id and peptide id.
     *
     * @param identId identification id.
     * @return int   peptide sequence start
     * @throws DataAccessException data access exception
     */
    @Override
    public int getPeptideSequenceStart(Comparable identId, Comparable peptideId) throws DataAccessException {
        int start = -1;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(ident, Integer.parseInt(peptideId.toString()));
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
     * @param identId identification id.
     * @return int   peptide sequence stop
     * @throws DataAccessException data access exception
     */
    @Override
    public int getPeptideSequenceEnd(Comparable identId, Comparable peptideId) throws DataAccessException {
        int stop = -1;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(ident, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                stop = peptide.getPeptideEvidenceList().get(0).getEndPosition();
                //Todo: We need to define finally how to manage the information for pride xml object as PeptideEvidence

            }
        }
        return stop;
    }

    /**
     * Get peptide spectrum id using identification id and peptide id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return int   peptide sequence stop
     * @throws DataAccessException data access exception
     */
    @Override
    public Comparable getPeptideSpectrumId(Comparable identId, Comparable peptideId) throws DataAccessException {
        Comparable specId = null;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(ident, Integer.parseInt(peptideId.toString()));
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
     * @param identId   identification id
     * @param peptideId peptide id, can be the index of the peptide
     * @return List<Modification>   a list of modifications.
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Modification> getPTMs(Comparable identId, Comparable peptideId) throws DataAccessException {
        List<Modification> mods = new ArrayList<Modification>();
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(ident, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                List<Modification> rawMods = peptide.getPeptideSequence().getModificationList();
                mods.addAll(rawMods);
            }
        }
        return mods;
    }

    @Override
    public Collection<SubstitutionModification> getSubstitutionPTMs(Comparable identId, Comparable peptideId) throws DataAccessException {
        List<SubstitutionModification> mods = new ArrayList<SubstitutionModification>();
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(ident, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                List<SubstitutionModification> rawMods = peptide.getPeptideSequence().getSubstitutionModificationList();
                mods.addAll(rawMods);
            }
        }
        return mods;
    }

    @Override
    public int getNumberOfSubstitutionPTMs(Comparable identId) throws DataAccessException {
        int cnt = 0;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            cnt = DataAccessUtilities.getNumberOfSubstitutionPTMs(ident);
        }
        return cnt;
    }

    @Override
    public int getNumberOfSubstitutionPTMs(Comparable identId, Comparable peptideId) throws DataAccessException {
        int cnt = 0;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            List<Peptide> peptides = ident.getIdentifiedPeptides();
            if (peptides != null) {
                Peptide peptide = peptides.get(Integer.parseInt(peptideId.toString()));
                if (peptide != null) {
                    cnt = DataAccessUtilities.getNumberOfSubstitutionPTMs(peptide);
                }
            }
        }
        return cnt;
    }

    @Override
    public int getNumberOfFragmentIons(Comparable identId, Comparable peptideId) throws DataAccessException {
        int num = 0;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(ident, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                List<FragmentIon> ions = peptide.getFragmentation();
                if (ions != null) {
                    num = ions.size();
                }
            }
        }
        return num;
    }

    @Override
    public Collection<FragmentIon> getFragmentIons(Comparable identId, Comparable peptideId) throws DataAccessException {
        List<FragmentIon> frags = new ArrayList<FragmentIon>();
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(ident, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                List<FragmentIon> rawFrags = peptide.getFragmentation();
                frags.addAll(rawFrags);
            }
        }
        return frags;
    }

    @Override
    public PeptideScore getPeptideScore(Comparable identId, Comparable peptideId) throws DataAccessException {
        PeptideScore score = null;
        Identification ident = getIdentificationById(identId);
        if (ident != null) {
            Peptide peptide = DataAccessUtilities.getPeptide(ident, Integer.parseInt(peptideId.toString()));
            if (peptide != null) {
                SearchEngine se = this.getSearchEngine();
                score = DataAccessUtilities.getPeptideScore(peptide, se.getSearchEngineTypes());
            }
        }
        return score;
    }

    @Override
    public synchronized Comparable getForegroundExperimentAcc() {
        return foregroundExperimentAcc;
    }

    @Override
    public void setForegroundExperimentAcc(Comparable expAcc) throws DataAccessException {
        logger.debug("Set foreground experiment accession: {}", expAcc);

        Comparable acc = foregroundExperimentAcc;
        if (!expAcc.equals(acc)) {
            // clear cache
            Comparable oldExp, newExp;
            synchronized (this) {
                oldExp = foregroundExperimentAcc;
                foregroundExperimentAcc = expAcc;
                newExp = foregroundExperimentAcc;
                populateCache();
            }
            firePropertyChange(FOREGROUND_EXPERIMENT_CHANGED, oldExp, newExp);
        }
    }

    @Override
    public synchronized Chromatogram getForegroundChromatogram() {
        return foregroundChromatogram;
    }

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

    @Override
    public synchronized Spectrum getForegroundSpectrum() {
        return foregroundSpectrum;
    }

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

    @Override
    public synchronized Identification getForegroundIdentification() {
        return foregroundIdentification;
    }

    @Override
    public void setForegroundIdentificationById(Comparable identId) throws DataAccessException {
        logger.debug("Set foreground identification id: {}", identId);

        Identification oldIdent = null;
        Identification newIdent = this.getIdentificationById(identId);
        synchronized (this) {
            if (foregroundIdentification == null ||
                    (newIdent != null && !foregroundIdentification.getId().equals(newIdent.getId()))) {
                oldIdent = foregroundIdentification;
                foregroundIdentification = getIdentificationById(identId);
            }
        }
        firePropertyChange(FOREGROUND_IDENTIFICATION_CHANGED, oldIdent, newIdent);
    }

    @Override
    public boolean hasQuantData() throws DataAccessException {
        Collection<QuantCvTermReference> methods = getQuantMethods();
        return methods.size() > 0;
    }

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

    @Override
    public boolean hasProteinTotalIntensities() throws DataAccessException {
        return getProteinQuantUnit() == null;
    }

    @Override
    public boolean hasPeptideTotalIntensities() throws DataAccessException {
        return getPeptideQuantUnit() == null;
    }

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

    @Override
    public boolean hasIsotopeLabellingQuantMethods() throws DataAccessException {
        // get the samples
        ParamGroup additionals = getAdditional();

        if (additionals != null) {
            // iterate over each sample
            List<CvParam> cvParams = additionals.getCvParams();
            for (CvParam cvParam : cvParams) {
                if (QuantCvTermReference.isIsotopeLabellingMethodParam(cvParam)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Collection<QuantCvTermReference> getQuantMethods() throws DataAccessException {
        Set<QuantCvTermReference> methods = new LinkedHashSet<QuantCvTermReference>();

        // get the samples
        ParamGroup additionals = getAdditional();

        if (additionals != null) {
            // iterate over each sample
            List<CvParam> cvParams = additionals.getCvParams();
            for (CvParam cvParam : cvParams) {
                if (QuantCvTermReference.isQuantitativeMethodParam(cvParam)) {
                    methods.add(QuantCvTermReference.getQuantitativeMethodParam(cvParam));
                }
            }
        }

        return methods;
    }

    @Override
    public Collection<QuantCvTermReference> getLabelFreeQuantMethods() throws DataAccessException {
        Set<QuantCvTermReference> methods = new LinkedHashSet<QuantCvTermReference>();

        // get the samples
        ParamGroup additionals = getAdditional();

        if (additionals != null) {
            // iterate over each sample
            List<CvParam> cvParams = additionals.getCvParams();
            for (CvParam cvParam : cvParams) {
                if (QuantCvTermReference.isLabelFreeMethod(cvParam)) {
                    methods.add(QuantCvTermReference.getQuantitativeMethodParam(cvParam));
                }
            }
        }

        return methods;
    }

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

    @Override
    public Collection<QuantCvTermReference> getIsotopeLabellingQuantMethods() throws DataAccessException {
        Set<QuantCvTermReference> methods = new LinkedHashSet<QuantCvTermReference>();

        // get the samples
        ParamGroup additionals = getAdditional();

        if (additionals != null) {
            // iterate over each sample
            List<CvParam> cvParams = additionals.getCvParams();
            for (CvParam cvParam : cvParams) {
                if (QuantCvTermReference.isIsotopeLabellingMethodParam(cvParam)) {
                    methods.add(QuantCvTermReference.getQuantitativeMethodParam(cvParam));
                }
            }
        }

        return methods;
    }

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
            int cnt = 20;
            if (hasProteinQuantData()) {
                Collection<Comparable> identIds = getIdentificationIds();

                for (Comparable identId : identIds) {
                    Quantitation quant = getProteinQuantData(identId);
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
                Collection<Comparable> identIds = getIdentificationIds();

                for (Comparable identId : identIds) {
                    Collection<Comparable> peptideIds = getPeptideIds(identId);
                    for (Comparable peptideId : peptideIds) {
                        Quantitation quant = getPeptideQuantData(identId, peptideId);
                        if (quant.hasTotalIntensities()) {
                            return index;
                        } else {
                            index = quant.getReferenceSubSampleIndex();
                            if (index > 0) {
                                break;
                            }
                        }
                    }
                    cnt --;
                    if (cnt == 0) {
                        break;
                    }
                }
            }
        }

        return index;
    }

    @Override
    public QuantitativeSample getQuantSample() throws DataAccessException {
        QuantitativeSample sampleDesc = new QuantitativeSample();

        Collection<Sample> samples = getSamples();
        if (samples != null && !samples.isEmpty()) {
            Sample sample = CollectionUtils.getElement(samples, 0);
            List<CvParam> cvParams = sample.getCvParams();
            // scan for all the species
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

        return sampleDesc;
    }

    /**
     * Note: this method will scan through first 10 identifications
     *
     * @return
     * @throws DataAccessException
     */
    @Override
    public QuantCvTermReference getProteinQuantUnit() throws DataAccessException {
        Collection<Comparable> identIds = getIdentificationIds();

        int cnt = 20;

        for (Comparable identId : identIds) {
            Quantitation quant = getProteinQuantData(identId);
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
     * Note: this method will scan all the peptides
     *
     * @return QuantCvTermReference    unit cv term
     * @throws DataAccessException data access exception
     */
    @Override
    public QuantCvTermReference getPeptideQuantUnit() throws DataAccessException {
        Collection<Comparable> identIds = getIdentificationIds();

        int cnt = 20;

        for (Comparable identId : identIds) {
            Collection<Comparable> peptideIds = getPeptideIds(identId);
            for (Comparable peptideId : peptideIds) {
                Quantitation quant = getPeptideQuantData(identId, peptideId);
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

    @Override
    public Quantitation getProteinQuantData(Comparable identId) throws DataAccessException {
        Identification ident = getIdentificationById(identId);
        return new Quantitation(Quantitation.Type.PROTEIN, ident.getCvParams());
    }

    @Override
    public Quantitation getPeptideQuantData(Comparable identId, Comparable peptideId) throws DataAccessException {
        Peptide peptide = getPeptideById(identId, peptideId);
        return new Quantitation(Quantitation.Type.PEPTIDE, peptide.getCvParams());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // empty method
    }

    @Override
    public ParamGroup getAdditional() throws DataAccessException {
        return null;
    }

}
