package uk.ac.ebi.pride.data.controller.impl.ControllerImpl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.cache.impl.MzIdentMLCacheBuilder;
import uk.ac.ebi.pride.data.controller.impl.Transformer.MzIdentMLTransformer;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.io.file.MzIdentMLUnmarshallerAdaptor;
import uk.ac.ebi.pride.data.utils.Constants;
import uk.ac.ebi.pride.data.utils.MD5Utils;
import uk.ac.ebi.pride.data.utils.MzIdentMLUtils;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The MzIdentMLControllerImpl is the controller that retrieve the information from
 * the mzidentml files. It have support for Experiment Metadata (Global metadata),
 * also it have information about the IdentificationMetadata. The MzGraphMetaData is not
 * supported for this files because they not contains information about spectrums. The controller
 * support the mzidentml schema version 1.1.
 * <p/>
 * User: yperez
 * Date: 19/09/11
 * Time: 16:08
 */
public class MzIdentMLControllerImpl extends CachedDataAccessController {

    // Logger property to trace the Errors
    private static final Logger logger = LoggerFactory.getLogger(MzIdentMLControllerImpl.class);

    //The unmarshller class that retrieve the information from the mzidentml files
    private MzIdentMLUnmarshallerAdaptor unmarshaller;

    // The Match pattern for a valid mzidentml file, its support now the version 1.1.
    private static Pattern mzIdentMLHeaderPattern = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<(MzIdentML)|(indexedmzIdentML) xmlns=.*", Pattern.MULTILINE);

    /*
      * This is a set of controllers related with the MS information in the mzidentml file
      * one or more controllers can be related with the same file formats. The Comparable
      * name of the file is an id of the file and the controller is the DataAccessController
       * related with the file.

     */
    private Map<Comparable, DataAccessController> msDataAccessControllers;

    /**
     * A map of the SpectraData associated with this mzidentml
     */

    private Map<Comparable, SpectraData> spectraDataMap;


    /**
     * The constructor used by Default the CACHE_AND_SOURCE mode, it
     * means retrieve information from cache first,
     * if didn't find anything,then read from data source directly.
     *
     * @param file
     * @throws DataAccessException
     */
    public MzIdentMLControllerImpl(File file) throws DataAccessException {
        this(file, null);
    }

    /**
     * Default Constructor extends the CacheDataAccessController
     *
     * @param file mzidentml file
     * @param mode if the the mode is using Cache or retrieving from Source
     * @throws DataAccessException
     */
    public MzIdentMLControllerImpl(File file, DataAccessMode mode) throws DataAccessException {
        super(file, mode);
        initialize();
    }

    /**
     * This function initialize all the Categories in which the Controller
     * used the Cache System. In this case it wil be use cache for PROTEIN,
     * PEPTIDE, SAMPLE and SOFTWARE.
     *
     * @throws DataAccessException
     */
    protected void initialize() throws DataAccessException {
        // create pride access utils
        File file = (File) getSource();
        unmarshaller = new MzIdentMLUnmarshallerAdaptor(file);

        // init ms data accession controller map
        this.msDataAccessControllers = new HashMap<Comparable, DataAccessController>();

        // init spectra data map
        this.spectraDataMap = new HashMap<Comparable, SpectraData>();

        // set data source description
        this.setName(file.getName());

        // set the type
        this.setType(Type.MZIDENTML);

        // set the content categories
        this.setContentCategories(
                ContentCategory.PROTEIN,
                ContentCategory.PEPTIDE,
                ContentCategory.SAMPLE,
                ContentCategory.SOFTWARE,
                ContentCategory.PROTEIN_GROUPS,
                ContentCategory.SPECTRUM
        );

        // set cache builder
        setCacheBuilder(new MzIdentMLCacheBuilder(this));

        // populate cache
        populateCache();

        populateGlobalObjects();
    }

    private void populateGlobalObjects() {
        List<CVLookup> CvParamList = MzIdentMLTransformer.transformCVList(unmarshaller.getCvList());
        MzIdentMLTransformer.setCvLookupMap(CvParamList);

    }

    /**
     * Return the mzidentml unmarshall adaptor to be used by the CacheBuilder
     * Implementation.
     *
     * @return MzIdentMLUnmarshallerAdaptor
     */
    public MzIdentMLUnmarshallerAdaptor getUnmarshaller() {
        return unmarshaller;
    }

    /**
     * Get the unique id of the data access controller
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
                String msg = "Failed to generate unique id for mzIdentML file";
                logger.error(msg, e);
            }
        }
        return uid;
    }

    /**
     * Get a list of cv lookup objects.
     *
     * @return List<CVLookup>   a list of cvlookup objects.
     * @throws DataAccessException
     */
    public List<CVLookup> getCvLookups() throws DataAccessException {
        return MzIdentMLTransformer.transformCVList(unmarshaller.getCvList());
    }

    /**
     * Get a list of source files.
     *
     * @return List<SourceFile> a list of source file objects.
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *
     */
    public List<SourceFile> getSourceFiles() throws DataAccessException {
        List<SourceFile> sourceFiles;
        try {
            sourceFiles = MzIdentMLTransformer.transformToSourceFile(unmarshaller.getSourceFiles());
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve source files", ex);
        }
        return sourceFiles;
    }

    /**
     * Get a list of Organization Contacts
     *
     * @return List<Organization> A List of Organizations
     * @throws DataAccessException
     */
    public List<Organization> getOrganizationContacts() throws DataAccessException {
        List<Organization> organizationList;
        try {
            organizationList = MzIdentMLTransformer.transformToOrganization(unmarshaller.getOrganizationContacts());
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve organization contacts", ex);
        }
        return organizationList;
    }

    /**
     * Get a list of Person Contacts
     *
     * @return List<Person> A list of Persons
     * @throws DataAccessException
     */
    public List<Person> getPersonContacts() throws DataAccessException {
        List<Person> personList;
        try {
            personList = MzIdentMLTransformer.transformToPerson(unmarshaller.getPersonContacts());
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve person contacts", ex);
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
            try {
                return MzIdentMLTransformer.transformToSample(unmarshaller.getSampleList());
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve samples", ex);
            }
        } else {
            return metaData.getSamples();
        }
    }

    /**
     * Get provider of the experiment
     *
     * @return Provider    data provider
     * @throws DataAccessException data access exception
     */
    public Provider getProvider() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();
        if (metaData == null) {
            return MzIdentMLTransformer.transformToProvider(unmarshaller.getProvider());
        }
        return metaData.getProvider();
    }

    /**
     * Get a list of softwares
     *
     * @return List<Software>   a list of software objects.
     * @throws DataAccessException data access exception
     */
    public List<Software> getSoftwares() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            try {
                return MzIdentMLTransformer.transformToSoftware(unmarshaller.getSoftwares());
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve software", ex);
            }
        } else {
            return metaData.getSoftwares();
        }
    }

    /**
     * Get a list of references
     *
     * @return List<Reference>  a list of reference objects
     * @throws DataAccessException data access exception
     */
    public List<Reference> getReferences() throws DataAccessException {
        List<Reference> refs;
        try {
            refs = MzIdentMLTransformer.transformToReference(unmarshaller.getReferences());
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve references", ex);
        }

        return refs;
    }

    /**
     * Additional is a concept that comes from PRIDE XML Files. In the mzidentml
     * all the concepts of the Additional comes inside different objects.
     * This function construct an Additional Object a relation of
     * creationDate, Original Spectra Data Files and finally the Original software
     * that provide the mzidentml file.
     *
     * @return ParamGroup   a group of cv parameters and user parameters.
     * @throws DataAccessException
     */
    @Override
    public ParamGroup getAdditional() throws DataAccessException {
        ParamGroup additionals = null;
        // Take information from provider !!!
        Provider provider = getProvider();
        Date date = unmarshaller.getCreationDate();
        List<SpectraData> spectraDataList = getSpectraDataFiles();

        if ((provider != null && provider.getSoftware() != null) || date != null || !spectraDataList.isEmpty()) {
            additionals = new ParamGroup();
            // Get information from last software that provide the file
            if (provider != null && provider.getSoftware() != null)
                additionals.addCvParams(provider.getSoftware().getCvParams());

            // Get the information of the creation file
            if (unmarshaller.getCreationDate() != null) {
                additionals.addCvParam(MzIdentMLTransformer.transformDateToCvParam(unmarshaller.getCreationDate()));
            }
            //Get spectra information as additional
            if (!spectraDataList.isEmpty()) {
                Set<CvParam> cvParamList = new HashSet<CvParam>();
                for (SpectraData spectraData : spectraDataList) {
                    if (spectraData.getSpectrumIdFormat() != null)
                        cvParamList.add(spectraData.getSpectrumIdFormat());
                    if (spectraData.getFileFormat() != null)
                        cvParamList.add(spectraData.getFileFormat());
                }
                List<CvParam> list = new ArrayList<CvParam>(cvParamList);
                additionals.addCvParams(list);
            }
        }
        return additionals;
    }

    /**
     * The mzidentml do not support Quatitation Data
     *
     * @return
     * @throws DataAccessException
     */
    @Override
    public boolean hasQuantData() throws DataAccessException {
        return false;
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
            try {
                // Get Accession for MzIdentML Object
                String accession = unmarshaller.getMzIdentMLId();
                // Get the Version of the MzIdentML File.
                String version = unmarshaller.getMzIdentMLVersion();
                //Get Source File List
                List<SourceFile> sources = getSourceFiles();
                //Get Sample List
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
                String title = unmarshaller.getMzIdentMLName();
                // Get The Experiment Short Label, in case of mzidentml this date is not provided.
                String shortLabel = null;
                //Get Experiment Protocol in case of mzidentml Experiment Protocol is empty.
                ExperimentProtocol protocol = null;
                // Get References From the Experiment
                List<Reference> references = getReferences();
                // Get the provider object of the MzIdentMl file
                Provider provider = getProvider();
                //Get Creation Date
                Date creationDate = unmarshaller.getCreationDate();
                //Create the ExperimentMetaData Object
                metaData = new ExperimentMetaData(additional, accession, title, version, shortLabel, samples, softwares,
                        persons, sources, provider, organizations, references, creationDate, null, protocol);
                // store it in the cache
                getCache().store(CacheCategory.EXPERIMENT_METADATA, metaData);
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve meta data", ex);
            }
        }

        return metaData;
    }

    /**
     * The spectrum IdentificationProtocol is the Set of parameters Related with
     * the Spectrum Identification Process in terms of Search Engines, Databases,
     * Enzymes, Modifications and Database Filters, etc
     *
     * @return List<SpectrumIdentificationProtocol> A List of Spectrum Identification Protocols
     * @throws DataAccessException
     */
    public List<SpectrumIdentificationProtocol> getSpectrumIdentificationProtocol() throws DataAccessException {
        IdentificationMetaData identificationMetaData = super.getIdentificationMetaData();

        if (identificationMetaData == null) {
            return MzIdentMLTransformer.transformToSpectrumIdentificationProtocol(unmarshaller.getSpectrumIdentificationProtcol());
        }
        return identificationMetaData.getSpectrumIdentificationProtocols();
    }

    /**
     * The Protein Protocol is a relation of different Software and Processing Steps with
     * the Identified Proteins.
     *
     * @return Protocol Protein Protocol
     * @throws DataAccessException
     */
    public Protocol getProteinDetectionProtocol() throws DataAccessException {
        IdentificationMetaData identificationMetaData = super.getIdentificationMetaData();
        if (identificationMetaData == null) {
            return MzIdentMLTransformer.transformToProteinDetectionProtocol(unmarshaller.getProteinDetectionProtocol());
        }
        return identificationMetaData.getProteinDetectionProtocol();
    }

    /**
     * Get the List of Databases used in the Experiment
     *
     * @return List<SearchDataBase> List of SearchDatabases
     * @throws DataAccessException
     */
    public List<SearchDataBase> getSearchDataBases() throws DataAccessException {
        IdentificationMetaData identificationMetaData = super.getIdentificationMetaData();
        if (identificationMetaData == null) {
            return MzIdentMLTransformer.transformToSearchDataBase(unmarshaller.getSearchDatabases());
        }
        return identificationMetaData.getSearchDataBases();
    }

    /**
     * The IdentificationMetadata is a Combination of SpectrumIdentificationProtocol,
     * a Protein Protocol and finally the Databases used in the Experiment.
     *
     * @return IdentificationMetadata the metadata related with the identification process
     * @throws DataAccessException
     */
    @Override
    public IdentificationMetaData getIdentificationMetaData() throws DataAccessException {
        IdentificationMetaData metaData = super.getIdentificationMetaData();
        if (metaData == null) {
            List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList = getSpectrumIdentificationProtocol();
            Protocol proteinDetectionProtocol = getProteinDetectionProtocol();
            List<SearchDataBase> searchDataBaseList = getSearchDataBases();
            metaData = new IdentificationMetaData(null, null, spectrumIdentificationProtocolList, proteinDetectionProtocol, searchDataBaseList);
        }
        return metaData;
    }

    /**
     * Get the List of File Spectras that the Mzidentml use to identified peptides
     *
     * @return
     * @throws DataAccessException
     */
    public List<SpectraData> getSpectraDataFiles() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();
        if (metaData == null) {
            return MzIdentMLTransformer.transformToSpectraData(unmarshaller.getSpectraData());
        }
        return metaData.getSpectraDatas();
    }

    /**
     * MzidemtML files will support in the future Spectra MetaData if is present
     * PRIDE Objects, also by other file Formats.
     *
     * @return
     * @throws DataAccessException
     */
    @Override
    public MzGraphMetaData getMzGraphMetaData() throws DataAccessException {
        return null;
    }

    /**
     * The MzGraphMetadata is not supported by mzidentml.
     *
     * @return
     * @throws DataAccessException
     */
    @Override
    public boolean hasSpectrum() throws DataAccessException {
        if (msDataAccessControllers != null) {
            for (Comparable id : msDataAccessControllers.keySet()) {
                if (msDataAccessControllers.get(id) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get identification using a identification id, gives the option to choose whether to use cache.
     * This implementation provides a way of by passing the cache.
     *
     * @param proteinId identification id
     * @param useCache  true means to use cache
     * @return Identification identification object
     * @throws DataAccessException data access exception
     */
    @Override
    public Protein getProteinById(Comparable proteinId, boolean useCache) throws DataAccessException {
        Protein ident = super.getProteinById(proteinId, useCache);

        if (ident == null && useCache) {

            logger.debug("Get new identification from file: {}", proteinId);

            try {
                // get protein hypothesis
                uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionHypothesis proteinHypothesis = getProteinDetectionHypothesis(proteinId);
                uk.ac.ebi.jmzidml.model.mzidml.DBSequence dbSequence = unmarshaller.getDBSequenceById(proteinId);
                uk.ac.ebi.jmzidml.model.mzidml.FragmentationTable fragmentationTable = unmarshaller.getFragmentationTable();

                if (proteinHypothesis == null) {
                    List<SpectrumIdentificationItem> spectrumIdentificationItems = getScannedSpectrumIdentificationItems(proteinId);
                    // todo: fragmentation table retrieving can be optimzied
                    ident = MzIdentMLTransformer.transformSpectrumIdentificationItemToIdentification(dbSequence, spectrumIdentificationItems, fragmentationTable);
                } else {
                    ident = MzIdentMLTransformer.transformProteinHypothesisToIdentification(proteinHypothesis, dbSequence, fragmentationTable);
                }

                if (ident != null) {
                    storeProteinToCache(ident);
                }
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve identification: " + proteinId, ex);
            }
        }

        return ident;
    }

    private List<SpectrumIdentificationItem> getScannedSpectrumIdentificationItems(Comparable proteinId) throws JAXBException {
        List<Comparable> spectrumIdentIds = null;

        if (getCache().hasCacheCategory(CacheCategory.PROTEIN_TO_PEPTIDE_EVIDENCES)) {
            Map<Comparable, Map<Comparable, Comparable>> mapPeptides = ((Map<Comparable, Map<Comparable, Map<Comparable, Comparable>>>) getCache().get(CacheCategory.PROTEIN_TO_PEPTIDE_EVIDENCES)).get(proteinId);
            spectrumIdentIds = new ArrayList<Comparable>(mapPeptides.keySet());
        }

        return unmarshaller.getSpectrumIdentificationsbyIds(spectrumIdentIds);
    }

    private uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionHypothesis getProteinDetectionHypothesis(Comparable proteinId) throws JAXBException {
        uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionHypothesis proteinDetectionHypothesis = null;

        if (getCache().hasCacheCategory(CacheCategory.PROTEIN_TO_PROTEIN_GROUP_ID)) {
            Comparable proteinHypothesisId = ((Map<Comparable, Comparable>) getCache().get(CacheCategory.PROTEIN_TO_PROTEIN_GROUP_ID)).get(proteinId);
            if (proteinHypothesisId != null) {
                proteinDetectionHypothesis = unmarshaller.getIdentificationById(proteinHypothesisId);
            }
        }

        return proteinDetectionHypothesis;
    }

    private void storeProteinToCache(Protein ident) {
        // store identification into cache
        getCache().store(CacheCategory.PROTEIN, ident.getId(), ident);
        // store precursor charge and m/z
        for (Peptide peptide : ident.getPeptides()) {
            getCache().store(CacheCategory.PEPTIDE, new Tuple<Comparable, Comparable>(ident.getId(), peptide.getSpectrumIdentification().getId()), peptide);
            if (hasSpectrum()) {
                Spectrum spectrum = getSpectrumById(peptide.getSpectrumIdentification().getId());
                spectrum.setPeptide(peptide);
                peptide.setSpectrum(spectrum);

                if (spectrum != null) {
                    getCache().store(CacheCategory.SPECTRUM_LEVEL_PRECURSOR_CHARGE, spectrum.getId(), DataAccessUtilities.getPrecursorCharge(spectrum));
                    getCache().store(CacheCategory.SPECTRUM_LEVEL_PRECURSOR_MZ, spectrum.getId(), DataAccessUtilities.getPrecursorMz(spectrum));
                }
            }
        }
    }


    /**
     * Get peptide using a given identification id and a given peptide index
     *
     * @param index    peptide index
     * @param useCache whether to use cache
     * @return Peptide  peptide
     * @throws DataAccessException exception while getting peptide
     */
    @Override
    public Peptide getPeptideByIndex(Comparable proteinId, Comparable index, boolean useCache) throws DataAccessException {
        Peptide peptide = super.getPeptideByIndex(proteinId, index, useCache);
        if (peptide == null || (peptide.getSpectrum() == null && hasSpectrum())) {
            logger.debug("Get new peptide from file: {}", index);
            Protein ident = getProteinById(proteinId);

            peptide = ident.getPeptides().get(Integer.parseInt(index.toString()));
            if (useCache && peptide != null) {
                getCache().store(CacheCategory.PEPTIDE, new Tuple<Comparable, Comparable>(proteinId, index), peptide);
                Spectrum spectrum = peptide.getSpectrum();
                if (hasSpectrum()) {
                    spectrum = getSpectrumById(peptide.getSpectrumIdentification().getId());
                    spectrum.setPeptide(peptide);
                    peptide.setSpectrum(spectrum);
                }
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
            num = unmarshaller.getNumIdentifiedPeptides();
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve number of peptides", ex);
        }
        return num;
    }

    @Override
    public void close() {
        unmarshaller = null;
        super.close();

    }


    /**
     * Check a file is MZIdentML XML file
     *
     * @param file input file
     * @return boolean true means MZIdentML XML
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
            Matcher matcher = mzIdentMLHeaderPattern.matcher(content);
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
    public boolean hasProteinGroup() throws DataAccessException {
        return super.getProteinGroupIds().size() > 0;
    }

    /**
     * Get spectrum using a spectrumIdentification id, gives the option to choose whether to
     * use cache. This implementation provides a way of by passing the cache.
     *
     * @param id       spectrum Identification ID
     * @param useCache true means to use cache
     * @return Spectrum spectrum object
     * @throws DataAccessException data access exception
     */
    @Override
    Spectrum getSpectrumById(Comparable id, boolean useCache) throws DataAccessException {
        String[] spectrumIdArray = ((Map<Comparable, String[]>) getCache().get(CacheCategory.PEPTIDE_TO_SPECTRUM)).get(id);

        /** To store in cache the Spectrum files, an Id was constructed using the spectrum ID and the
         *  id of the File.
         **/
        Comparable spectrumId;
        if (spectrumIdArray != null && spectrumIdArray.length > 0) {
            spectrumId = spectrumIdArray[0] + "!" + spectrumIdArray[1];
        } else {
            spectrumId = id;
            spectrumIdArray = ((String) spectrumId).split("!");
        }

        Spectrum spectrum = super.getSpectrumById(spectrumId, useCache);
        if (spectrum == null && id != null) {
            logger.debug("Get new spectrum from file: {}", id);
            try {
                DataAccessController spectrumController = msDataAccessControllers.get(spectrumIdArray[1]);
                if (spectrumController != null) {
                    spectrum = spectrumController.getSpectrumById(spectrumIdArray[0]);
                    if (useCache && spectrum != null) {
                        getCache().store(CacheCategory.SPECTRUM, id, spectrum);
                    }
                }
            } catch (Exception ex) {
                String msg = "Error while getting spectrum: " + id;
                logger.error(msg, ex);
                throw new DataAccessException(msg, ex);
            }
        }
        return spectrum;
    }

    @Override
    public Collection<Comparable> getSpectrumIds() throws DataAccessException {
        Collection<Comparable> spectrumIds = super.getSpectrumIds();
        if (spectrumIds.size() == 0 && hasSpectrum()) {
            spectrumIds = new ArrayList<Comparable>();
            for (Comparable id : msDataAccessControllers.keySet()) {
                if (msDataAccessControllers.get(id) != null)
                    for (Comparable idSpectrum : msDataAccessControllers.get(id).getSpectrumIds()) {
                        spectrumIds.add(idSpectrum + "!" + id);
                    }
            }
        }
        return spectrumIds;
    }

    public void addMSController(List<File> dataAccessControllerFiles) throws DataAccessException {
        Map<SpectraData, File> spectraDataFileMap = checkMScontrollers(dataAccessControllerFiles);
        if (!spectraDataFileMap.isEmpty()) {
            for (SpectraData spectraData : spectraDataFileMap.keySet()) {
                msDataAccessControllers.put(spectraData.getId(), new PeakControllerImpl(spectraDataFileMap.get(spectraData)));
            }
            //Todo: Other controller more than mgf
        }
    }

    public void addMSController(Map<SpectraData, File> spectraDataFileMap) throws DataAccessException {

        Map<SpectraData, File> spectraDataControllerMap = getSpectraDataMSFiles();

        for (SpectraData spectraData : spectraDataControllerMap.keySet()) {
            for (SpectraData spectraDataFile : spectraDataFileMap.keySet()) {
                if (spectraDataControllerMap.get(spectraData) == null &&
                        spectraData.getId().compareTo(spectraDataFile.getId()) == 0) {
                    if (MzIdentMLUtils.getSpectraDataFormat(spectraData) == Constants.SpecFileFormat.MZXML)
                        msDataAccessControllers.put(spectraData.getId(), new MzXmlControllerImpl(spectraDataFileMap.get(spectraDataFile)));
                    if (MzIdentMLUtils.getSpectraDataFormat(spectraData) == Constants.SpecFileFormat.MGF)
                        msDataAccessControllers.put(spectraData.getId(), new PeakControllerImpl(spectraDataFileMap.get(spectraDataFile)));
                    if (MzIdentMLUtils.getSpectraDataFormat(spectraData) == Constants.SpecFileFormat.MZML)
                        msDataAccessControllers.put(spectraData.getId(), new MzMLControllerImpl(spectraDataFileMap.get(spectraDataFile)));
                    //Todo: Need to check if changes

                }
            }

        }
    }

    public Map<SpectraData, File> checkMScontrollers(List<File> dataAccessControllerFiles) throws DataAccessException {

        if (spectraDataMap.isEmpty()) {
            spectraDataMap = getSpectraDataMap();
        }

        Map<SpectraData, File> spectraFileMap = new HashMap<SpectraData, File>();

        for (File file : dataAccessControllerFiles) {
            for (Comparable id : spectraDataMap.keySet()) {
                SpectraData spectraData = spectraDataMap.get(id);
                if (spectraData.getLocation().indexOf(file.getName()) > 0) {
                    spectraFileMap.put(spectraData, file);
                }
            }
        }
        return spectraFileMap;
    }

    private Map<Comparable, SpectraData> getSpectraDataMap() throws DataAccessException {
        Map<Comparable, List<Comparable>> spectraDataIdMap = (Map<Comparable, List<Comparable>>) getCache().get(CacheCategory.SPECTRADATA_TO_SPECTRUMIDS);

        Set<Comparable> spectraDataIds = spectraDataIdMap.keySet();
        Map<Comparable, SpectraData> spectraDataMapResult = new HashMap<Comparable, SpectraData>();
        try {
            Map<Comparable, uk.ac.ebi.jmzidml.model.mzidml.SpectraData> oldSpectraDataMap = unmarshaller.getSpectraData(spectraDataIds);
            for (Comparable id : oldSpectraDataMap.keySet()) {
                SpectraData spectraData = MzIdentMLTransformer.transformToSpectraData(oldSpectraDataMap.get(id));
                if (isSpectraDataSupported(spectraData)) {
                    spectraDataMapResult.put(id, spectraData);
                }
            }
        } catch (JAXBException ex) {
            String msg = "Error while getting the spectrum File information";
            logger.error(msg, ex);
            throw new DataAccessException(msg, ex);
        }
        return spectraDataMapResult;
    }

    private boolean isSpectraDataSupported(SpectraData spectraData) {
        return (!(MzIdentMLUtils.getSpectraDataIdFormat(spectraData) == Constants.SpecIdFormat.NONE ||
                MzIdentMLUtils.getSpectraDataFormat(spectraData) == Constants.SpecFileFormat.NONE));
    }

    public Map<SpectraData, DataAccessController> getSpectraDataMSControllers() throws DataAccessException {
        if (spectraDataMap.isEmpty()) {
            spectraDataMap = getSpectraDataMap();
        }

        Map<SpectraData, DataAccessController> mapResult = new HashMap<SpectraData, DataAccessController>();

        for (Comparable id : spectraDataMap.keySet()) {
            if (msDataAccessControllers.containsKey(id)) {
                mapResult.put(spectraDataMap.get(id), msDataAccessControllers.get(id));
            } else {
                mapResult.put(spectraDataMap.get(id), null);
            }
        }
        return mapResult;
    }

    public Map<SpectraData, File> getSpectraDataMSFiles() throws DataAccessException {
        Map<SpectraData, DataAccessController> spectraDataControllerMAp = getSpectraDataMSControllers();
        Map<SpectraData, File> spectraDataFileMap = new HashMap<SpectraData, File>();
        for (SpectraData spectraData : spectraDataControllerMAp.keySet()) {
            DataAccessController controller = spectraDataControllerMAp.get(spectraData);
            spectraDataFileMap.put(spectraData, (controller == null) ? null : (File) controller.getSource());
        }
        return spectraDataFileMap;
    }

    public Integer getNumberOfSpectrabySpectraData(SpectraData spectraData) {
        Map<Comparable, List<Comparable>> spectraDataIdMap = (Map<Comparable, List<Comparable>>) getCache().get(CacheCategory.SPECTRADATA_TO_SPECTRUMIDS);
        return spectraDataIdMap.get(spectraData.getId()).size();
    }

    /**
     * Get the number of spectra
     *
     * @return int the number of spectra
     * @throws DataAccessException data access exception
     */

    @Override
    public int getNumberOfSpectra() throws DataAccessException {
        int numberOfSpectra = 0;
        if (!msDataAccessControllers.isEmpty()) {
            for (Comparable idMsFile : msDataAccessControllers.keySet()) {
                if (msDataAccessControllers.get(idMsFile) != null) {
                    numberOfSpectra += msDataAccessControllers.get(idMsFile).getNumberOfSpectra();
                }
            }
        }
        return numberOfSpectra;
    }

    @Override
    public int getNumberOfIdentifiedSpectra() throws DataAccessException {
        Map<Comparable, List<Comparable>> spectraDataIdMap = (Map<Comparable, List<Comparable>>) getCache().get(CacheCategory.SPECTRADATA_TO_SPECTRUMIDS);
        int countSpectra = 0;
        for (Comparable id : spectraDataIdMap.keySet()) {
            countSpectra = +spectraDataIdMap.get(id).size();
        }
        return countSpectra;
    }

    public List<DataAccessController> getSpectrumDataAccessControllers() {
        return new ArrayList<DataAccessController>(msDataAccessControllers.values());
    }


}
