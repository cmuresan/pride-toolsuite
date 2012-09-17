package uk.ac.ebi.pride.data.controller.impl.ControllerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.cache.impl.PrideXmlCacheBuilder;
import uk.ac.ebi.pride.data.controller.impl.Transformer.PrideXmlTransformer;
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
            String msg = "Error while getting cv lookups";
            logger.error(msg, ex);
            throw new DataAccessException(msg, ex);
        }
        return cvLookups;
    }


    /**
     * Get a list of source files.
     *
     * @return List<SourceFile> a list of source file objects.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *
     */
    @Override
    public List<SourceFile> getSourceFiles() throws DataAccessException {
        List<SourceFile> sourceFiles = new ArrayList<SourceFile>();

        try {
            SourceFile sourceFile = PrideXmlTransformer.transformSourceFile(reader.getAdmin());
            if (sourceFile != null) {
                sourceFiles.add(sourceFile);
            }
        } catch (Exception ex) {
            String msg = "Error while getting source files";
            logger.error(msg, ex);
            throw new DataAccessException(msg, ex);
        }

        return sourceFiles;
    }

    @Override
    public List<Organization> getOrganizationContacts() throws DataAccessException {
        logger.debug("Get organizational contact");
        List<Organization> organizationList = new ArrayList<Organization>();
        try {
            organizationList.addAll(PrideXmlTransformer.transformContactToOrganization(reader.getAdmin()));
        } catch (Exception ex) {
            String msg = "Error while getting organizational contacts";
            logger.error(msg, ex);
            throw new DataAccessException(msg, ex);
        }
        return organizationList;
    }

    @Override
    public List<Person> getPersonContacts() throws DataAccessException {
        logger.debug("Get person contacts");
        List<Person> personList = new ArrayList<Person>();
        try {
            personList.addAll(PrideXmlTransformer.transformContactToPerson(reader.getAdmin()));
        } catch (Exception ex) {
            String msg = "Error while getting person contacts";
            logger.error(msg, ex);
            throw new DataAccessException(msg, ex);
        }
        return personList;
    }

    /**
     * Get a list of samples
     *
     * @return List<Sample> a list of sample objects.
     * @throws DataAccessException
     */
    @Override
    public List<Sample> getSamples() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            logger.error("Get samples");
            List<Sample> samples = new ArrayList<Sample>();
            try {
                Sample sample = PrideXmlTransformer.transformSample(reader.getAdmin());
                if (sample != null) {
                    samples.add(sample);
                }
                return samples;
            } catch (Exception ex) {
                String msg = "Error while getting samples";
                logger.error(msg, ex);
                throw new DataAccessException(msg, ex);
            }
        } else {
            return metaData.getSampleList();
        }
    }

    /**
     * Get a list of software
     *
     * @return List<Software>   a list of software objects.
     * @throws DataAccessException
     */
    @Override
    public List<Software> getSoftwares() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

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
                String msg = "Error while getting software list";
                logger.error(msg, ex);
                throw new DataAccessException(msg, ex);
            }
        } else {
            return metaData.getSoftwares();
        }
    }

    /**
     * Get a list of instruments
     *
     * @return List<Instrument> a list of instruments.
     * @throws DataAccessException
     */
    @Override
    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

        if (metaData == null) {
            logger.debug("Get instrument configurations");
            List<InstrumentConfiguration> configs = new ArrayList<InstrumentConfiguration>();
            try {
                configs.addAll(PrideXmlTransformer.transformInstrument(reader.getInstrument(), reader.getDataProcessing()));
                return configs;
            } catch (Exception ex) {
                String msg = "Error while getting instrument configurations";
                logger.error(msg, ex);
                throw new DataAccessException(msg, ex);
            }
        } else {
            return metaData.getInstrumentConfigurations();
        }
    }

    /**
     * Get a list of data processing objects
     *
     * @return List<DataProcessing> a list of data processing objects
     * @throws DataAccessException
     */
    @Override
    public List<DataProcessing> getDataProcessings() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();

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
                String msg = "Error while getting data processings";
                logger.error(msg, ex);
                throw new DataAccessException(msg, ex);
            }
        } else {
            return metaData.getDataProcessingList();
        }
    }

    /**
     * Get a list of references
     *
     * @return List<Reference>  a list of reference objects
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *
     */
    public List<Reference> getReferences() throws DataAccessException {
        logger.debug("Get references");
        List<Reference> refs = new ArrayList<Reference>();

        try {
            refs.addAll(PrideXmlTransformer.transformReferences(reader.getReferences()));
        } catch (Exception ex) {
            String msg = "Error while getting references";
            logger.error(msg, ex);
            throw new DataAccessException(msg, ex);
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
    public ExperimentProtocol getProtocol() throws DataAccessException {
        logger.debug("Get protocol");
        try {
            return PrideXmlTransformer.transformProtocol(reader.getProtocol());
        } catch (Exception ex) {
            String msg = "Error while getting protocol";
            logger.error(msg, ex);
            throw new DataAccessException(msg, ex);
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
        ExperimentMetaData metaData = super.getExperimentMetaData();
        if (metaData == null) {
            logger.debug("Get additional params");
            try {
                return PrideXmlTransformer.transformAdditional(reader.getAdditionalParams());
            } catch (Exception ex) {
                String msg = "Error while getting additional params";
                logger.error(msg, ex);
                throw new DataAccessException(msg, ex);
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
    public ExperimentMetaData getExperimentMetaData() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            logger.debug("Get metadata");
            try {
                // Get Accession for Pride XML Object
                String accession = reader.getExpAccession();
                // Get the Version of the Pride File.
                String version = reader.getVersion();
                //Get Source File List
                List<SourceFile> sources = getSourceFiles();
                // Get Samples objects for PRide Object
                List<Sample> samples = getSamples();
                // Get all the softwares related with the object
                List<Software> softwares = getSoftwares();
                // Get Contact Persons
                List<Person> persons = getPersonContacts();
                // Get the Contact Organization
                List<Organization> organizations = getOrganizationContacts();
                // Get Additional Information Related with the Project
                ParamGroup additional = getAdditional();
                // Get the Experiment Title
                String title = reader.getExpTitle();
                // Get The Experiment Short Label
                String shortLabel = reader.getExpShortLabel();
                //Get Experiment Protocol
                ExperimentProtocol protocol = getProtocol();
                // Get References From the Experiment
                List<Reference> references = getReferences();

                metaData = new ExperimentMetaData(additional, accession, title, version, shortLabel, samples, softwares, persons, sources, null, organizations, references, null, null, protocol);
                // store it in the cache
                getCache().store(CacheCategory.EXPERIMENT_METADATA, metaData);
            } catch (Exception ex) {
                String msg = "Error while getting experiment meta data";
                logger.error(msg, ex);
                throw new DataAccessException(msg, ex);
            }
        }

        return metaData;
    }

    @Override
    public IdentificationMetaData getIdentificationMetaData() throws DataAccessException {
//        IdentificationMetaData metaData = super.getIdentificationMetaData();
//        if (metaData == null) {
//            List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList = null;
//            //Todo: Try to convert the CVTerms in Pride to SpectrumIdentificationProtocol
//            Protocol proteinDetectionProtocol = null;
//            //Todo: Try to convert the CVTerms in Pride to Protocol
//            List<SearchDataBase> searchDataBaseList = null;
//            //Todo: We need to search in the peptides Identifications all of the Search Databases Used.
//            //Todo: We need to search all of the possible modifications presented in the experiment.
//
//            metaData = new IdentificationMetaData(null, null, spectrumIdentificationProtocolList, proteinDetectionProtocol, searchDataBaseList);
//        }
        //return metaData;
        return null;
    }

    @Override
    public MzGraphMetaData getMzGraphMetaData() throws DataAccessException {
        MzGraphMetaData metaData = super.getMzGraphMetaData();
        if (metaData == null) {
            List<ScanSetting> scanSettings = null;
            List<DataProcessing> dataProcessings = getDataProcessings();
            List<InstrumentConfiguration> instrumentConfigurations = getInstrumentConfigurations();
            metaData = new MzGraphMetaData(null, null, scanSettings, instrumentConfigurations, dataProcessings);
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
    Spectrum getSpectrumById(Comparable id, boolean useCache) throws DataAccessException {
        Spectrum spectrum = super.getSpectrumById(id, useCache);
        if (spectrum == null && id != null) {
            logger.debug("Get new spectrum from file: {}", id);
            try {
                spectrum = PrideXmlTransformer.transformSpectrum(reader.getSpectrumById(id.toString()));
                if (useCache && spectrum != null) {
                    getCache().store(CacheCategory.SPECTRUM, id, spectrum);
                }
            } catch (Exception ex) {
                String msg = "Error while getting spectrum: " + id;
                logger.error(msg, ex);
                throw new DataAccessException(msg, ex);
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
     * @param proteinId       identification id
     * @param useCache true means to use cache
     * @return Identification identification object
     * @throws DataAccessException data access exception
     */
    @Override
    public Protein getProteinById(Comparable proteinId, boolean useCache) throws DataAccessException {
        Protein ident = super.getProteinById(proteinId, useCache);
        if (ident == null) {
            logger.debug("Get new identification from file: {}", proteinId);
            try {
                ident = PrideXmlTransformer.transformIdentification(reader.getIdentById(proteinId.toString()));
                if (useCache && ident != null) {
                    // store identification into cache
                    getCache().store(CacheCategory.PROTEIN, proteinId, ident);
                    // store precursor charge and m/z
                    for (Peptide peptide : ident.getPeptides()) {
                        Spectrum spectrum = peptide.getSpectrum();
                        if (spectrum != null) {
                            getCache().store(CacheCategory.SPECTRUM_LEVEL_PRECURSOR_CHARGE, spectrum.getId(), DataAccessUtilities.getPrecursorCharge(spectrum));
                            getCache().store(CacheCategory.SPECTRUM_LEVEL_PRECURSOR_MZ, spectrum.getId(), DataAccessUtilities.getPrecursorMz(spectrum));
                        }
                    }
                }
            } catch (Exception ex) {
                String msg = "Error while getting identification: " + proteinId;
                logger.error(msg, ex);
                throw new DataAccessException(msg, ex);
            }
        }
        return ident;
    }


    /**
     * Get peptide using a given identification id and a given peptide index
     *
     * @param proteinId  identification id
     * @param index    peptide index
     * @param useCache whether to use cache
     * @return Peptide  peptide
     * @throws DataAccessException exception while getting peptide
     */
    @Override
    public Peptide getPeptideByIndex(Comparable proteinId, Comparable index, boolean useCache) throws DataAccessException {
        Peptide peptide = super.getPeptideByIndex(proteinId, index, useCache);
        if (peptide == null) {
            logger.debug("Get new peptide from file: {}-{}", proteinId, index);
            peptide = PrideXmlTransformer.transformPeptide(reader.getPeptide(proteinId.toString(), Integer.parseInt(index.toString())));
            if (useCache && peptide != null) {
                // store peptide
                getCache().store(CacheCategory.PEPTIDE, new Tuple<Comparable, Comparable>(proteinId, index), peptide);
                // store precursor charge and m/z
                Spectrum spectrum = peptide.getSpectrum();
                if (spectrum != null) {
                    getCache().store(CacheCategory.SPECTRUM_LEVEL_PRECURSOR_CHARGE, spectrum.getId(), DataAccessUtilities.getPrecursorCharge(spectrum));
                    getCache().store(CacheCategory.SPECTRUM_LEVEL_PRECURSOR_MZ, spectrum.getId(), DataAccessUtilities.getPrecursorMz(spectrum));
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

    @Override
    public Collection<Comparable> getProteinGroupIds() throws DataAccessException {
        return null;
    }
}
