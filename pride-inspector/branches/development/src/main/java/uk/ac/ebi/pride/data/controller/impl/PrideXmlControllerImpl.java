package uk.ac.ebi.pride.data.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.cache.impl.PrideXmlCacheBuilder;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.utils.MD5Utils;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PrideXmlControllerImpl is responsible for reading Pride Xml files.
 * <p/>
 * User: rwang
 * Date: 02-Feb-2010
 * Time: 12:31:30
 */
public class PrideXmlControllerImpl extends CachedDataAccessController {
    private static final Logger logger = LoggerFactory.getLogger(PrideXmlControllerImpl.class);
    /**
     * Pattern for match pride xml format
     */
    private static final Pattern prideXmlHeaderPattern = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<ExperimentCollection [^>]*>", Pattern.MULTILINE);
    /**
     * Reader to get information from pride xml file
     */
    private PrideXmlReader reader = null;

    /**
     * Construct a data access controller to read a pride xml
     *
     * @param file pride xml
     * @throws DataAccessException data access controller
     */
    public PrideXmlControllerImpl(File file) throws DataAccessException {
        this(file, null);
    }

    /**
     * Construct a data access controller with a pride xml and a given data access mode
     *
     * @param file pride xml file
     * @param mode data access mode
     * @throws DataAccessException data access exception
     */
    public PrideXmlControllerImpl(File file, DataAccessMode mode) throws DataAccessException {
        super(file, mode);
        initialize();
    }

    /**
     * Initialize data access controller
     *
     * @throws DataAccessException data access exception
     */
    protected void initialize() throws DataAccessException {
        // create pride access utils
        File file = (File) getSource();
        reader = new PrideXmlReader(file);
        // set data source description
        this.setName(file.getName());
        // set the type
        this.setType(Type.XML_FILE);
        // set the content categories
        this.setContentCategories(ContentCategory.SPECTRUM,
                ContentCategory.PROTEIN,
                ContentCategory.PEPTIDE,
                ContentCategory.SAMPLE,
                ContentCategory.PROTOCOL,
                ContentCategory.INSTRUMENT,
                ContentCategory.SOFTWARE,
                ContentCategory.DATA_PROCESSING,
                ContentCategory.QUANTITATION);
        // set cache builder
        setCacheBuilder(new PrideXmlCacheBuilder(this));
        // populate cache
        populateCache();
    }

    /**
     * Get the pride xml reader
     *
     * @return PrideXmlReader  pride xml reader
     */
    public PrideXmlReader getReader() {
        return reader;
    }

    /**
     * Get md5 hash unique id
     *
     * @return String  unique id
     */
    @Override
    public String getUid() {
        String uid = super.getUid();
        if (uid == null) {
            // create a new UUID
            File file = (File) this.getSource();
            try {
                uid = MD5Utils.generateHash(file.getAbsolutePath());
            } catch (NoSuchAlgorithmException e) {
                String msg = "Failed to generate unique id for mzML file";
                logger.error(msg, e);
            }
        }
        return uid;
    }

    /**
     * Get a list of cv lookup objects.
     *
     * @return List<CVLookup>   a list of cvlookup objects.
     * @throws DataAccessException data access exception
     */
    @Override
    public List<CVLookup> getCvLookups() throws DataAccessException {
        logger.debug("Get cv lookups");
        List<CVLookup> cvLookups = new ArrayList<CVLookup>();
        try {
            cvLookups.addAll(PrideXmlTransformer.transformCvLookups(reader.getCvLookups()));
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve cv lookups", ex);
        }
        return cvLookups;
    }


    /**
     * Get the FileDescription object
     *
     * @return FileDescription  FileDescription object.
     * @throws DataAccessException data access exception
     */
    @Override
    public FileDescription getFileDescription() throws DataAccessException {
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            logger.debug("Get file description");
            FileDescription fileDesc;

            try {
                List<SourceFile> sourceFiles = getSourceFiles();
                List<ParamGroup> contacts = getContacts();
                fileDesc = new FileDescription(null, sourceFiles, contacts);
                return fileDesc;
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve file description", ex);
            }
        } else {
            return metaData.getFileDescription();
        }
    }

    /**
     * Get referenceable param group, pride xml does not contain this kind of information
     *
     * @return ReferenceableParamGroup referenceable param group
     * @throws DataAccessException data access exception
     */
    @Override
    public ReferenceableParamGroup getReferenceableParamGroup() throws DataAccessException {
        throw new UnsupportedOperationException("This method is unsupported");
    }

    /**
     * Get a list of source files.
     *
     * @return List<SourceFile> a list of source file objects.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *
     */
    private List<SourceFile> getSourceFiles() throws DataAccessException {
        logger.debug("Get source files");
        List<SourceFile> sourceFiles = new ArrayList<SourceFile>();

        try {
            SourceFile sourceFile = PrideXmlTransformer.transformSourceFile(reader.getAdmin());
            if (sourceFile != null) {
                sourceFiles.add(sourceFile);
            }
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve source files", ex);
        }

        return sourceFiles;
    }

    /**
     * Get a list of contact details
     *
     * @return List<ParamGroup> a list of parameter groups.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *
     */
    private List<ParamGroup> getContacts() throws DataAccessException {
        logger.debug("Get contacts");
        List<ParamGroup> contacts = new ArrayList<ParamGroup>();

        try {
            contacts.addAll(PrideXmlTransformer.transformContacts(reader.getAdmin()));
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve contacts", ex);
        }

        return contacts;
    }

    /**
     * Get a list of samples
     *
     * @return List<Sample> a list of sample objects.
     * @throws DataAccessException
     */
    @Override
    public List<Sample> getSamples() throws DataAccessException {
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            logger.debug("Get samples");
            List<Sample> samples = new ArrayList<Sample>();
            try {
                Sample sample = PrideXmlTransformer.transformSample(reader.getAdmin());
                if (sample != null) {
                    samples.add(sample);
                }
                return samples;
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve samples", ex);
            }
        } else {
            return metaData.getSamples();
        }
    }

    /**
     * Get a list of software
     *
     * @return List<Software>   a list of software objects.
     * @throws DataAccessException
     */
    @Override
    public List<Software> getSoftware() throws DataAccessException {
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            logger.debug("Get software");
            List<Software> softwares = new ArrayList<Software>();
            try {
                Software software = PrideXmlTransformer.transformSoftware(reader.getDataProcessing());
                if (software != null) {
                    softwares.add(software);
                }
                return softwares;
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve software", ex);
            }
        } else {
            return metaData.getSoftwares();
        }
    }

    /**
     * Get a list of scan settings, pride xml does not contain this kind of information
     *
     * @return Collection<ScanSetting> a collection of scan settings
     * @throws DataAccessException data access exception
     */
    @Override
    public Collection<ScanSetting> getScanSettings() throws DataAccessException {
        throw new UnsupportedOperationException("This method is unsupported");
    }

    /**
     * Get a list of instruments
     *
     * @return List<Instrument> a list of instruments.
     * @throws DataAccessException
     */
    @Override
    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            logger.debug("Get instrument configurations");
            List<InstrumentConfiguration> configs = new ArrayList<InstrumentConfiguration>();
            try {
                configs.addAll(PrideXmlTransformer.transformInstrument(reader.getInstrument(), reader.getDataProcessing()));
                return configs;
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve instrument configuration", ex);
            }
        } else {
            return metaData.getInstrumentConfigurations();
        }
    }

    /**
     * Get a list of data processing objects
     *
     * @return List<DataProcessing> a list of data processing objects
     * @throws DataAccessException data access exception
     */
    @Override
    public List<DataProcessing> getDataProcessings() throws DataAccessException {
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            logger.debug("Get data processings");
            List<DataProcessing> dataProcessings = new ArrayList<DataProcessing>();
            try {
                DataProcessing dataProcessing = PrideXmlTransformer.transformDataProcessing(reader.getDataProcessing());
                if (dataProcessing != null) {
                    dataProcessings.add(dataProcessing);
                }
                return dataProcessings;
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve data processings", ex);
            }
        } else {
            return metaData.getDataProcessings();
        }
    }

    /**
     * Get a list of references
     *
     * @return List<Reference>  a list of reference objects
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          data access exception
     */
    private List<Reference> getReferences() throws DataAccessException {
        logger.debug("Get references");
        List<Reference> refs = new ArrayList<Reference>();

        try {
            refs.addAll(PrideXmlTransformer.transformReferences(reader.getReferences()));
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve references", ex);
        }

        return refs;
    }

    /**
     * Get the protocol object
     *
     * @return Protocol protocol object.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *
     */
    private Protocol getProtocol() throws DataAccessException {
        logger.debug("Get protocol");
        try {
            return PrideXmlTransformer.transformProtocol(reader.getProtocol());
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve protocol", ex);
        }

    }

    /**
     * Get additional parameters
     *
     * @return ParamGroup   a group of cv parameters and user parameters.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *
     */
    @Override
    public ParamGroup getAdditional() throws DataAccessException {
        MetaData metaData = super.getMetaData();
        if (metaData == null) {
            logger.debug("Get additional params");
            try {
                return PrideXmlTransformer.transformAdditional(reader.getAdditionalParams());
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve additional information", ex);
            }
        } else {
            return metaData;
        }
    }

    /**
     * Get meta data related to this experiment
     *
     * @return MetaData meta data object
     * @throws DataAccessException data access exception
     */
    @Override
    public MetaData getMetaData() throws DataAccessException {
        MetaData metaData = super.getMetaData();

        if (metaData == null) {
            logger.debug("Get metadata");
            try {
                String accession = reader.getExpAccession();
                String version = reader.getVersion();
                FileDescription fileDesc = getFileDescription();
                List<Sample> samples = getSamples();
                List<Software> software = getSoftware();
                List<InstrumentConfiguration> instrumentConfigurations = getInstrumentConfigurations();
                List<DataProcessing> dataProcessings = getDataProcessings();
                ParamGroup additional = getAdditional();
                String title = reader.getExpTitle();
                String shortLabel = reader.getExpShortLabel();
                Protocol protocol = getProtocol();
                List<Reference> references = getReferences();
                metaData = new Experiment(null, accession, version, fileDesc,
                        samples, software, null, instrumentConfigurations,
                        dataProcessings, additional, title, shortLabel,
                        protocol, references, null, null);
                // store it in the cache
                getCache().store(CacheCategory.EXPERIMENT_METADATA, metaData);
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve meta data", ex);
            }
        }

        return metaData;
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
    @Override
    protected Spectrum getSpectrumById(Comparable id, boolean useCache) throws DataAccessException {
        Spectrum spectrum = super.getSpectrumById(id, useCache);
        if (spectrum == null && id != null) {
            logger.debug("Get new spectrum from file: {}", id);
            try {
                spectrum = PrideXmlTransformer.transformSpectrum(reader.getSpectrumById(id.toString()));
                if (useCache && spectrum != null) {
                    getCache().store(CacheCategory.SPECTRUM, id, spectrum);
                }
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve spectrum: " + id, ex);
            }
        }
        return spectrum;
    }

    /**
     * Check whether the spectrum has been identified.
     *
     * @param specId spectrum id
     * @return boolean     true means identified
     * @throws DataAccessException data access exception
     */
    @Override
    public boolean isIdentifiedSpectrum(Comparable specId) throws DataAccessException {
        return reader.isIdentifiedSpectrum(specId.toString());
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
    @Override
    public Identification getIdentificationById(Comparable id, boolean useCache) throws DataAccessException {
        Identification ident = super.getIdentificationById(id, useCache);
        if (ident == null) {
            logger.debug("Get new identification from file: {}", id);
            try {
                ident = PrideXmlTransformer.transformIdentification(reader.getIdentById(id.toString()));
                if (useCache && ident != null) {
                    // store identification into cache
                    getCache().store(CacheCategory.IDENTIFICATION, id, ident);
                    // store precursor charge and m/z
                    for (Peptide peptide : ident.getPeptides()) {
                        Spectrum spectrum = peptide.getSpectrum();
                        if (spectrum != null) {
                            getCache().store(CacheCategory.PRECURSOR_CHARGE, spectrum.getId(), DataAccessUtilities.getPrecursorCharge(spectrum));
                            getCache().store(CacheCategory.PRECURSOR_MZ, spectrum.getId(), DataAccessUtilities.getPrecursorMz(spectrum));
                        }
                    }
                }
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve identification: " + id, ex);
            }
        }
        return ident;
    }


    /**
     * Get peptide using a given identification id and a given peptide index
     *
     * @param identId  identification id
     * @param index    peptide index
     * @param useCache whether to use cache
     * @return Peptide  peptide
     * @throws DataAccessException exception while getting peptide
     */
    @Override
    public Peptide getPeptideById(Comparable identId, Comparable index, boolean useCache) throws DataAccessException {
        Peptide peptide = super.getPeptideById(identId, index, useCache);
        if (peptide == null) {
            logger.debug("Get new peptide from file: {}-{}", identId, index);
            peptide = PrideXmlTransformer.transformPeptide(reader.getPeptide(identId.toString(), Integer.parseInt(index.toString())));
            if (useCache && peptide != null) {
                // store peptide
                getCache().store(CacheCategory.PEPTIDE, new Tuple<Comparable, Comparable>(identId, index), peptide);
                // store precursor charge and m/z
                Spectrum spectrum = peptide.getSpectrum();
                if (spectrum != null) {
                    getCache().store(CacheCategory.PRECURSOR_CHARGE, spectrum.getId(), DataAccessUtilities.getPrecursorCharge(spectrum));
                    getCache().store(CacheCategory.PRECURSOR_MZ, spectrum.getId(), DataAccessUtilities.getPrecursorMz(spectrum));
                }
            }
        }
        return peptide;
    }

    /**
     * Get the number of peptides.
     *
     * @return int  the number of peptides.
     * @throws DataAccessException data access exception.
     */
    @Override
    public int getNumberOfPeptides() throws DataAccessException {
        int num;
        try {
            // this method is overridden to use the reader directly
            num = reader.getNumberOfPeptides();
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve number of peptides", ex);
        }
        return num;
    }

    @Override
    public void close() {
        reader = null;
        super.close();
    }

    /**
     * Check a file is PRIDE XML file
     *
     * @param file input file
     * @return boolean true means PRIDE XML
     */
    public static boolean isValidFormat(File file) {
        boolean valid = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            // read the first ten lines
            StringBuilder content = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                content.append(reader.readLine());
            }
            // check file type
            Matcher matcher = prideXmlHeaderPattern.matcher(content);
            valid = matcher.find();
        } catch (Exception e) {
            logger.error("Failed to read file", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing here
                }
            }
        }

        return valid;
    }
}
