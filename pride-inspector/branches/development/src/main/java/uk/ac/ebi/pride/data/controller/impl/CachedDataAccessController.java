package uk.ac.ebi.pride.data.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartFactory;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;
import uk.ac.ebi.pride.chart.model.implementation.SpectralDataPerExperimentException;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.controller.access.CacheAccess;
import uk.ac.ebi.pride.data.controller.cache.Cache;
import uk.ac.ebi.pride.data.controller.cache.CacheAccessor;
import uk.ac.ebi.pride.data.controller.cache.CacheBuilder;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.data.utils.QuantCvTermReference;
import uk.ac.ebi.pride.gui.component.chart.PrideChartManager;

import java.util.*;

/**
 * CachedDataAccessController is abstract class, which enables caching for DataAccessController.
 * Cache is used to store the information used to populate GUI's tables.
 * <p/>
 * It provides the option to choose different running mode, at the present, there are two modes:
 * <p/>
 * 1.CACHE_ONLY: only get the information from cache.
 * 2.CACHE_AND_SOURCE: it first checks the cache, if not exist, then read from data source.
 * <p/>
 * User: rwang
 * Date: 13-Sep-2010
 * Time: 14:26:03
 */
public abstract class CachedDataAccessController extends AbstractDataAccessController implements CacheAccess {
    private static final Logger logger = LoggerFactory.getLogger(CachedDataAccessController.class);
    /**
     * the default data access mode is to use both cache and data source
     */
    private static final DataAccessMode DEFAULT_ACCESS_MODE = DataAccessMode.CACHE_AND_SOURCE;
    /**
     * data access mode
     */
    private DataAccessMode mode;
    /**
     * Note: this cache is related to each experiment, must be reset when switching experiment.
     */
    private Cache cache;
    /**
     * builder is responsible for initializing the Cache
     */
    private CacheBuilder cacheBuilder;

    /**
     * Construct a data access controller to use both the cache and the source
     */
    public CachedDataAccessController() {
        this(null, DEFAULT_ACCESS_MODE);
    }

    /**
     * Construct a data access controller using a given access mode
     *
     * @param mode
     */
    public CachedDataAccessController(DataAccessMode mode) {
        this(null, mode);
    }

    /**
     * Constructor a data access controller using a given data source and access mode.
     *
     * @param source data source
     * @param mode   DataAccessMode
     */
    public CachedDataAccessController(Object source, DataAccessMode mode) {
        super(source);
        this.mode = mode;
        this.cache = new CacheAccessor();
    }

    /**
     * Get the existing cache
     *
     * @return Cache cache, note: this returns the actual cache object.
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Set a new cache
     *
     * @param cache new cache
     */
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * Get the current cache builder.
     *
     * @return CacheBuilder cache builder
     */
    public CacheBuilder getCacheBuilder() {
        return cacheBuilder;
    }

    /**
     * Set cache builder.
     *
     * @param builder cache builder
     */
    public void setCacheBuilder(CacheBuilder builder) {
        this.cacheBuilder = builder;
    }

    /**
     * Clear the current cache
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Populate the cache with values.
     *
     * @throws DataAccessException data access exception
     */
    public void populateCache() throws DataAccessException {
        if (cacheBuilder != null) {
            try {
                cacheBuilder.populate();
            } catch (Exception e) {
                String msg = "Exception while trying to populate cache";
                logger.error(msg, e);
                throw new DataAccessException(msg, e);
            }
        }
    }

    /**
     * Get the runtime mode
     *
     * @return mode    runtime mode
     */
    @Override
    public DataAccessMode getMode() {
        return mode;
    }

    /**
     * Set the runtime mode
     *
     * @param mode DataAccessMode
     */
    @Override
    public void setMode(DataAccessMode mode) {
        this.mode = mode;
    }


    /**
     * Get experiment accessions from cache
     *
     * @return Collection<Comparable>   a collection of experiment accessions.
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Comparable> getExperimentAccs() throws DataAccessException {
        return (Collection<Comparable>) cache.get(CacheCategory.EXPERIMENT_ACC);
    }

    /**
     * Get experimental metadata
     *
     * @return MetaData    meta data
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public MetaData getMetaData() throws DataAccessException {
        Collection<MetaData> metaDatas = (Collection<MetaData>) cache.get(CacheCategory.EXPERIMENT_METADATA);

        if (metaDatas != null && !metaDatas.isEmpty()) {
            return CollectionUtils.getElement(metaDatas, 0);
        }

        return null;
    }

    /**
     * Get spectrum ids from cache
     *
     * @return Collection<Comparable>  a collection of spectrum ids
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Comparable> getSpectrumIds() throws DataAccessException {
        Collection<Comparable> ids = (Collection<Comparable>) cache.get(CacheCategory.SPECTRUM_ID);
        return ids == null ? Collections.<Comparable>emptyList() : ids;
    }

    /**
     * Get chromatogram ids from cache
     *
     * @return Collection<Comparable>  a collection of chromatogram ids.
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Comparable> getChromatogramIds() throws DataAccessException {
        Collection<Comparable> ids = (Collection<Comparable>) cache.get(CacheCategory.CHROMATOGRAM_ID);
        return ids == null ? Collections.<Comparable>emptyList() : ids;
    }

    /**
     * Get identification ids from cache
     *
     * @return Collection<Comparable>  a collection of identification ids.
     * @throws DataAccessException data access exception
     */
    @SuppressWarnings("unchecked")
    public Collection<Comparable> getIdentificationIds() throws DataAccessException {
        Collection<Comparable> ids = (Collection<Comparable>) cache.get(CacheCategory.IDENTIFICATION_ID);
        return ids == null ? Collections.<Comparable>emptyList() : ids;
    }

    /**
     * Get spectrum object from cache
     * It uses cache by default
     *
     * @param id Spectrum id
     * @return Spectrum spectrum
     * @throws DataAccessException data access exception
     */
    @Override
    public Spectrum getSpectrumById(Comparable id) throws DataAccessException {
        return getSpectrumById(id, true);
    }

    /**
     * Get spectrum using a spectrum id, gives the option to choose whether to use cache.
     * This implementation provides a way of by passing the cache.
     *
     * @param id       spectrum id
     * @param useCache true means to use cache
     * @return Spectrum spectrum object
     * @throws DataAccessException data access exception
     */
    protected Spectrum getSpectrumById(Comparable id, boolean useCache) throws DataAccessException {
        return useCache ? (Spectrum) cache.get(CacheCategory.SPECTRUM, id) : null;
    }

    /**
     * Get chromatogram object form cache
     * It uses cache by default
     *
     * @param id chromatogram string id
     * @return Chromatogram chromatogram object
     * @throws DataAccessException data access exception
     */
    @Override
    public Chromatogram getChromatogramById(Comparable id) throws DataAccessException {
        return getChromatogramById(id, true);
    }

    /**
     * Get chromatogram using a chromatogram id, gives the option to choose whether to use cache.
     * This implementation provides a way of by passing the cache.
     *
     * @param id       chromatogram id
     * @param useCache true means to use cache
     * @return Chromatogram chromatogram object
     * @throws DataAccessException data access exception
     */
    public Chromatogram getChromatogramById(Comparable id, boolean useCache) throws DataAccessException {
        return useCache ? (Chromatogram) cache.get(CacheCategory.CHROMATOGRAM, id) : null;
    }

    /**
     * Get identification object from cache
     * It uses the cache by default
     *
     * @param id a string id of Identification
     * @return Identification  identification object
     * @throws DataAccessException data access exception
     */
    @Override
    public Identification getIdentificationById(Comparable id) throws DataAccessException {
        return getIdentificationById(id, true);
    }

    /**
     * Get identification using a identification id, gives the option to choose whether to use cache.
     * This implementation provides a way of by passing the cache.
     *
     * @param id       identification id
     * @param useCache true means to use cache
     * @return Identification identification object
     * @throws DataAccessException data access exception
     */
    protected Identification getIdentificationById(Comparable id, boolean useCache) throws DataAccessException {
        return useCache ? (Identification) cache.get(CacheCategory.IDENTIFICATION, id) : null;
    }


    /**
     * Get number of peaks using spectrum id.
     * This implementation will check cache first.
     *
     * @param specId spectrum id.
     * @return int number of peaks
     * @throws DataAccessException data access exception
     */
    @Override
    public int getNumberOfPeaks(Comparable specId) throws DataAccessException {
        Integer numOfPeaks = (Integer) cache.get(CacheCategory.NUMBER_OF_PEAKS, specId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && numOfPeaks == null) {
            numOfPeaks = super.getNumberOfPeaks(specId);
            cache.store(CacheCategory.NUMBER_OF_PEAKS, specId, numOfPeaks);
        }
        return numOfPeaks == null ? 0 : numOfPeaks;
    }

    /**
     * Get ms level using spectrum id.
     * This implementation will check cache first. if return -1, means ms level
     * doesn't exist.
     *
     * @param specId spectrum id.
     * @return int ms level
     * @throws DataAccessException data access exception
     */
    @Override
    public int getMsLevel(Comparable specId) throws DataAccessException {
        Integer msLevel = (Integer) cache.get(CacheCategory.MS_LEVEL, specId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && msLevel == null) {
            msLevel = super.getMsLevel(specId);
            cache.store(CacheCategory.MS_LEVEL, specId, msLevel);
        }
        return msLevel == null ? -1 : msLevel;
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
    public Integer getPrecursorCharge(Comparable specId) throws DataAccessException {
        Integer charge = (Integer) cache.get(CacheCategory.PRECURSOR_CHARGE, specId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && charge == null) {
            charge = super.getPrecursorCharge(specId);
            cache.store(CacheCategory.PRECURSOR_CHARGE, specId, charge);
        }
        return charge;
    }

    /**
     * Get precursor m/z value using spectrum id.
     * This implementation will check cache first.
     *
     * @param specId spectrum id.
     * @return double m/z
     * @throws DataAccessException data access exception
     */
    @Override
    public double getPrecursorMz(Comparable specId) throws DataAccessException {
        Double mz = (Double) cache.get(CacheCategory.PRECURSOR_MZ, specId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && mz == null) {
            mz = super.getPrecursorMz(specId);
            cache.store(CacheCategory.PRECURSOR_MZ, specId, mz);
        }
        return mz == null ? -1 : mz;
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
        Double intent = (Double) cache.get(CacheCategory.PRECURSOR_INTENSITY, specId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && intent == null) {
            intent = super.getPrecursorIntensity(specId);
            cache.store(CacheCategory.PRECURSOR_INTENSITY, specId, intent);
        }
        return intent == null ? -1 : intent;
    }

    /**
     * Get sum of intensity value using spectrum id.
     * This implementation will check cache first.
     *
     * @param specId spectrum id.
     * @return double sum of intensity
     * @throws DataAccessException data access exception
     */
    @Override
    public double getSumOfIntensity(Comparable specId) throws DataAccessException {
        Double sum = (Double) cache.get(CacheCategory.SUM_OF_INTENSITY, specId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && sum == null) {
            sum = super.getSumOfIntensity(specId);
            cache.store(CacheCategory.SUM_OF_INTENSITY, specId, sum);
        }
        return sum;
    }

    /**
     * Get protein accession value using identification id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return String protein accession
     * @throws DataAccessException data access exception
     */
    @Override
    public String getProteinAccession(Comparable identId) throws DataAccessException {
        String acc = (String) cache.get(CacheCategory.PROTEIN_ACCESSION, identId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && acc == null) {
            acc = super.getProteinAccession(identId);
            cache.store(CacheCategory.PROTEIN_ACCESSION, identId, acc);
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
        String accVersion = (String) cache.get(CacheCategory.PROTEIN_ACCESSION_VERSION, identId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && accVersion == null) {
            accVersion = super.getProteinAccessionVersion(identId);
            if (accVersion != null) {
                cache.store(CacheCategory.PROTEIN_ACCESSION_VERSION, identId, accVersion);
            }
        }
        return accVersion;
    }

    /**
     * Get identification score using identification id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return double identification score
     * @throws DataAccessException data access exception
     */
    @Override
    public double getIdentificationScore(Comparable identId) throws DataAccessException {
        Double score = (Double) cache.get(CacheCategory.SCORE, identId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && score == null) {
            score = super.getIdentificationScore(identId);
            cache.store(CacheCategory.SCORE, identId, score);
        }
        return score == null ? -1 : score;
    }

    /**
     * Get identification threshold using identification id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return double sum of intensity
     * @throws DataAccessException data access exception
     */
    @Override
    public double getIdentificationThreshold(Comparable identId) throws DataAccessException {
        Double threshold = (Double) cache.get(CacheCategory.THRESHOLD, identId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && threshold == null) {
            threshold = super.getIdentificationThreshold(identId);
            cache.store(CacheCategory.THRESHOLD, identId, threshold);
        }
        return threshold == null ? -1 : threshold;
    }

    /**
     * Get search database using identification id
     *
     * @param identId identification id.
     * @return String search database
     * @throws DataAccessException data accession exception
     */
    @Override
    public String getSearchDatabase(Comparable identId) throws DataAccessException {
        String database = (String) cache.get(CacheCategory.PROTEIN_SEARCH_DATABASE, identId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && database == null) {
            database = super.getSearchDatabase(identId);
            if (database != null) {
                cache.store(CacheCategory.PROTEIN_SEARCH_DATABASE, identId, database);
            }
        }
        return database;
    }

    /**
     * Get search database version using identification id
     *
     * @param identId identification id.
     * @return String search database version
     * @throws DataAccessException data accession exception
     */
    @Override
    public String getSearchDatabaseVersion(Comparable identId) throws DataAccessException {
        String version = (String) cache.get(CacheCategory.PROTEIN_SEARCH_DATABASE_VERSION, identId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && version == null) {
            version = super.getSearchDatabase(identId);
            if (version != null) {
                cache.store(CacheCategory.PROTEIN_SEARCH_DATABASE_VERSION, identId, version);
            }
        }
        return version;
    }

    /**
     * Get peptide ids using identification id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return Collection<Comparable>   peptide ids collection
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Comparable> getPeptideIds(Comparable identId) throws DataAccessException {
        Collection<Comparable> ids = (List<Comparable>) cache.get(CacheCategory.IDENTIFICATION_TO_PEPTIDE, identId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && ids == null) {
            ids = super.getPeptideIds(identId);
            cache.store(CacheCategory.IDENTIFICATION_TO_PEPTIDE, identId, ids);
        }
        return ids;
    }

    @Override
    public Peptide getPeptideById(Comparable identId, Comparable index) throws DataAccessException {
        return getPeptideById(identId, index, true);
    }

    public Peptide getPeptideById(Comparable identId, Comparable index, boolean useCache) throws DataAccessException {
        Peptide pep = null;

        if (useCache) {
            // check whether the identification exist in the cache already
            Identification ident = (Identification) cache.get(CacheCategory.IDENTIFICATION, identId);
            if (ident != null) {
                int indexInt = Integer.parseInt(index.toString());
                List<Peptide> peptides = ident.getPeptides();
                if (indexInt >= 0 && indexInt < peptides.size()) {
                    pep = peptides.get(indexInt);
                }
            } else {
                pep = (Peptide) cache.get(CacheCategory.PEPTIDE, new Tuple<Comparable, Comparable>(identId, index));
            }
        }

        return pep;
    }

    /**
     * Get peptide sequences using identification id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return Collection<Comparable>   peptide ids collection
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPeptideSequences(Comparable identId) throws DataAccessException {
        List<Comparable> ids = (List<Comparable>) cache.get(CacheCategory.IDENTIFICATION_TO_PEPTIDE, identId);
        List<String> sequences = new ArrayList<String>();
        if (ids != null && cache.hasCacheCategory(CacheCategory.PEPTIDE_SEQUENCE)) {
            // get each peptide sequence one by one from cache
            Collection<String> seqs = (Collection<String>) cache.getInBatch(CacheCategory.PEPTIDE_SEQUENCE, ids);
            sequences.addAll(seqs);
        } else if (!DataAccessMode.CACHE_ONLY.equals(mode)) {
            // read from data source
            sequences = super.getPeptideSequences(identId);
            // note: peptide id is not supported by pride xml
            // this is why we didn't store them to cache
        }

        return sequences;
    }

    /**
     * Get number of peptides using identification id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return int   number of peptides
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfPeptides(Comparable identId) throws DataAccessException {
        int cnt = 0;
        List<Comparable> ids = (List<Comparable>) cache.get(CacheCategory.IDENTIFICATION_TO_PEPTIDE, identId);
        if (ids != null) {
            cnt = ids.size();
        } else if (!DataAccessMode.CACHE_ONLY.equals(mode)) {
            cnt = super.getNumberOfPeptides(identId);
        }
        return cnt;
    }

    /**
     * Get number of unique peptides using identification id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return int   number of unique peptides
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfUniquePeptides(Comparable identId) throws DataAccessException {
        int cnt = 0;
        List<Comparable> ids = (List<Comparable>) cache.get(CacheCategory.IDENTIFICATION_TO_PEPTIDE, identId);
        if (ids != null && cache.hasCacheCategory(CacheCategory.PEPTIDE_SEQUENCE)) {
            Collection<String> seqs = (Collection<String>) cache.getInBatch(CacheCategory.PEPTIDE_SEQUENCE, ids);
            cnt = (new HashSet<String>(seqs)).size();
        } else if (!DataAccessMode.CACHE_ONLY.equals(mode)) {
            cnt = super.getNumberOfUniquePeptides(identId);
        }
        return cnt;
    }

    /**
     * Get number of ptms using identification id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return int   number of ptms
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfPTMs(Comparable identId) throws DataAccessException {
        int cnt = 0;
        List<Comparable> ids = (List<Comparable>) cache.get(CacheCategory.IDENTIFICATION_TO_PEPTIDE, identId);
        if (ids != null && cache.hasCacheCategory(CacheCategory.PEPTIDE_TO_MODIFICATION)) {
            // get all ptm locations
            Collection<List<Tuple<String, Integer>>> ptms = (Collection<List<Tuple<String, Integer>>>) cache.getInBatch(CacheCategory.PEPTIDE_TO_MODIFICATION, ids);
            for (List<Tuple<String, Integer>> ptm : ptms) {
                cnt += ptm.size();
            }
        } else if (!DataAccessMode.CACHE_ONLY.equals(mode)) {
            cnt = super.getNumberOfPTMs(identId);
        }

        return cnt;
    }

    /**
     * Get number of ptms using peptide id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return int   number of ptms
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public int getNumberOfPTMs(Comparable identId, Comparable peptideId) throws DataAccessException {
        int cnt = 0;
        List<Tuple<String, Integer>> locations = (List<Tuple<String, Integer>>) cache.get(CacheCategory.PEPTIDE_TO_MODIFICATION, peptideId);
        if (locations != null) {
            cnt = locations.size();
        } else if (!DataAccessMode.CACHE_ONLY.equals(mode)) {
            cnt = super.getNumberOfPTMs(identId, peptideId);
        }

        return cnt;
    }

    /**
     * Get peptide sequence using identification id and peptide id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return int   number of unique peptides
     * @throws DataAccessException data access exception
     */
    @Override
    public String getPeptideSequence(Comparable identId, Comparable peptideId) throws DataAccessException {
        String seq = (String) cache.get(CacheCategory.PEPTIDE_SEQUENCE, peptideId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && seq == null) {
            seq = super.getPeptideSequence(identId, peptideId);
            // note: peptide id is not supported by pride xml
            // this is why we didn't store them to cache
        }
        return seq;
    }

    /**
     * Get peptide sequence start using identification id and peptide id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return int   peptide sequence start
     * @throws DataAccessException data access exception
     */
    @Override
    public int getPeptideSequenceStart(Comparable identId, Comparable peptideId) throws DataAccessException {
        Integer start = (Integer) cache.get(CacheCategory.PEPTIDE_START, peptideId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && start == null) {
            start = super.getPeptideSequenceStart(identId, peptideId);
            // note: peptide id is not supported by pride xml
            // this is why we didn't store them to cache
        }
        return start == null ? -1 : start;
    }

    /**
     * Get peptide sequence stop using identification id and peptide id.
     * This implementation will check cache first.
     *
     * @param identId identification id.
     * @return int   peptide sequence stop
     * @throws DataAccessException data access exception
     */
    @Override
    public int getPeptideSequenceEnd(Comparable identId, Comparable peptideId) throws DataAccessException {
        Integer stop = (Integer) cache.get(CacheCategory.PEPTIDE_END, peptideId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && stop == null) {
            stop = super.getPeptideSequenceEnd(identId, peptideId);
            // note: peptide id is not supported by pride xml
            // this is why we didn't store them to cache
        }
        return stop == null ? -1 : stop;
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
        Comparable specId = (Comparable) cache.get(CacheCategory.PEPTIDE_TO_SPECTRUM, peptideId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && specId == null) {
            specId = super.getPeptideSpectrumId(identId, peptideId);
            // note: peptide id is not supported by pride xml
            // this is why we didn't store them to cache
        }
        return specId;
    }

    /**
     * Get ptms using identification id and peptide id
     *
     * @param identId   identification id
     * @param peptideId peptide id, can be the index of the peptide
     * @return List<Modification>   a list of modifications.
     * @throws DataAccessException data access exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Modification> getPTMs(Comparable identId, Comparable peptideId) throws DataAccessException {
        List<Tuple<String, Integer>> ptms = (List<Tuple<String, Integer>>) cache.get(CacheCategory.PEPTIDE_TO_MODIFICATION, peptideId);
        List<Modification> mods = new ArrayList<Modification>();

        if (ptms != null) {
            // create modification from cache
            for (Tuple<String, Integer> ptm : ptms) {
                String modAcc = ptm.getKey();
                Integer location = ptm.getValue();
                Modification mod = (Modification) cache.get(CacheCategory.MODIFICATION, modAcc);
                Modification newMod = new Modification(mod, modAcc, mod.getModDatabase(), mod.getModDatabaseVersion(),
                        mod.getMonoMassDeltas(), mod.getAvgMassDeltas(), location);
                mods.add(newMod);
            }
        } else if (!DataAccessMode.CACHE_ONLY.equals(mode)) {
            mods = super.getPTMs(identId, peptideId);
        }
        return mods;
    }

    /**
     * Get the number of fragment ions in a given peptide
     *
     * @param identId   identification id
     * @param peptideId peptide id, can be the index of the peptide as well.
     * @return int number of fragment ions
     * @throws DataAccessException data access controller
     */
    @Override
    public int getNumberOfFragmentIons(Comparable identId, Comparable peptideId) throws DataAccessException {
        Integer num = (Integer) cache.get(CacheCategory.NUMBER_OF_FRAGMENT_IONS, peptideId);
        if (!DataAccessMode.CACHE_ONLY.equals(mode) && num == null) {
            num = super.getNumberOfFragmentIons(identId, peptideId);
        }
        return num == null ? 0 : num;
    }

    /**
     * Get peptide score from search engine
     *
     * @param identId   identification id
     * @param peptideId peptide id, can be the index of the peptide as well.
     * @return PeptideScore    peptide score from search engine
     * @throws DataAccessException data access exception
     */
    @Override
    public PeptideScore getPeptideScore(Comparable identId, Comparable peptideId) throws DataAccessException {
        PeptideScore score = null;
        // get peptide additional parameters
        ParamGroup paramGroup = (ParamGroup) cache.get(CacheCategory.PEPTIDE_TO_PARAM, peptideId);
        if (paramGroup != null) {
            // get peptide score
            SearchEngine se = this.getSearchEngine();
            score = DataAccessUtilities.getPeptideScore(paramGroup, se.getSearchEngineTypes());
        } else if (!DataAccessMode.CACHE_ONLY.equals(mode)) {
            score = super.getPeptideScore(identId, peptideId);
        }

        return score;
    }

    /**
     * Get precursor charge on peptide level
     * Note: sometimes, precursor charge at the peptide level is different from the precursor charge at the spectrum level
     * As the peptide-level precursor charge is often assigned by search engine rather than ms instrument
     *
     * @param identId   identification id
     * @param peptideId peptid eid, can be the index of the peptide as well.
     * @return precursor charge, 0 should be returned if not available
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          data access exception
     */
    @Override
    public Integer getPeptidePrecursorCharge(Comparable identId, Comparable peptideId) throws DataAccessException {
        Integer charge = null;
        // get peptide additional parameters
        ParamGroup paramGroup = (ParamGroup) cache.get(CacheCategory.PEPTIDE_TO_PARAM, peptideId);
        if (paramGroup != null) {
            // get peptide precursor charge
            charge = DataAccessUtilities.getPrecursorCharge(paramGroup);
        } else if (!DataAccessMode.CACHE_ONLY.equals(mode)) {
            charge = super.getPeptidePrecursorCharge(identId, peptideId);
        }

        return charge;
    }

    /**
     * Get search engine type
     *
     * @return SearchEngine    search engine
     * @throws DataAccessException data access exception
     */
    @Override
    public SearchEngine getSearchEngine() throws DataAccessException {
        Collection<SearchEngine> searchEngines = (Collection<SearchEngine>) cache.get(CacheCategory.SEARCH_ENGINE_TYPE);

        if (searchEngines != null && !searchEngines.isEmpty()) {
            return CollectionUtils.getElement(searchEngines, 0);
        } else if (!DataAccessMode.CACHE_ONLY.equals(mode)) {
            return super.getSearchEngine();
        }

        return null;
    }

    /**
     * Get chart data for generating chart component
     *
     * @return List<PrideChartManager> a list of chart data
     * @throws DataAccessException data access exception
     */
    @Override
    public List<PrideChartManager> getChartData() throws DataAccessException {
        ExperimentSummaryData spectralSummaryData;
        try {
            spectralSummaryData = new PrideChartSummaryData(this);
        } catch (SpectralDataPerExperimentException e) {
            String msg = "PrideChartSummaryData object could not be created";
            logger.error(msg, e);
            return new ArrayList<PrideChartManager>(); //An empty list
        }

        List<PrideChartManager> list = new ArrayList<PrideChartManager>();
        for (PrideChart prideChart : PrideChartFactory.getAllCharts(spectralSummaryData)) {
            list.add(new PrideChartManager(prideChart));
        }
        return list;
    }

    /**
     * Get protein quantification unit
     *
     * @return QuantCvTermReference    quantification unit
     * @throws DataAccessException data access exception
     */
    @Override
    public QuantCvTermReference getProteinQuantUnit() throws DataAccessException {
        Collection<QuantCvTermReference> units = (Collection<QuantCvTermReference>) cache.get(CacheCategory.PROTEIN_QUANT_UNIT);

        if (units != null && !units.isEmpty()) {
            return CollectionUtils.getElement(units, 0);
        } else {
            QuantCvTermReference unit = super.getProteinQuantUnit();
            cache.store(CacheCategory.PROTEIN_QUANT_UNIT, unit);
            return unit;
        }
    }

    /**
     * Get peptide quantification unit
     *
     * @return QuantCvTermReference    quantification unit
     * @throws DataAccessException data access exception
     */
    @Override
    public QuantCvTermReference getPeptideQuantUnit() throws DataAccessException {
        Collection<QuantCvTermReference> units = (Collection<QuantCvTermReference>) cache.get(CacheCategory.PEPTIDE_QUANT_UNIT);

        if (units != null && !units.isEmpty()) {
            return CollectionUtils.getElement(units, 0);
        } else {
            QuantCvTermReference unit = super.getPeptideQuantUnit();
            cache.store(CacheCategory.PEPTIDE_QUANT_UNIT, unit);
            return unit;
        }
    }

    /**
     * Close data access controller by clearing the cache first
     */
    @Override
    public void close() {
        clearCache();
        super.close();
    }
}