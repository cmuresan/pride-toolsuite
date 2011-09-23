package uk.ac.ebi.pride.data.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.cache.impl.MzIdentMLCacheBuilder;
import uk.ac.ebi.pride.data.coreIdent.*;
import uk.ac.ebi.pride.data.io.file.MzIdentMLUnmarshallerAdaptor;
import uk.ac.ebi.pride.data.utils.MD5Utils;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 19/09/11
 * Time: 16:08
 */
public class MzIdentMLControllerImpl extends CachedDataAccessController {

    private static final Logger logger = LoggerFactory.getLogger(MzIdentMLControllerImpl.class);

    private MzIdentMLUnmarshallerAdaptor unmarshaller = null;

    private static Pattern mzIdentMLHeaderPattern = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<(mzML)|(indexedmzML) xmlns=.*", Pattern.MULTILINE);

    public MzIdentMLControllerImpl(File file) throws DataAccessException {
        this(file, null);
    }

    public MzIdentMLControllerImpl(File file, DataAccessMode mode) throws DataAccessException {
        super(file, mode);
        initialize();
    }

    protected void initialize() throws DataAccessException {
        // create pride access utils
        File file = (File) getSource();
        MzIdentMLUnmarshaller um = new MzIdentMLUnmarshaller(file);
        unmarshaller = new MzIdentMLUnmarshallerAdaptor(um);
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
        setCacheBuilder(new MzIdentMLCacheBuilder(this));
        // populate cache
        populateCache();
        // create pride xml transformer
        //MzIdentMLTransformer.setSpectrumIds(new ArrayList<Comparable>(this.getSpectrumIds()));
    }

    public MzIdentMLUnmarshallerAdaptor getUnmarshaller(){
        return unmarshaller;
    }

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
     * @throws DataAccessException
     */
   // @Override
    /*public List<CVLookup> getCvLookups() throws DataAccessException {
        List<CVLookup> cvLookups = new ArrayList<CVLookup>();
        try {
            cvLookups.addAll(MzIdentMLTransformer.transformCvLookups(unmarshaller.getCvLookups()));
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve cv lookups", ex);
        }
        return cvLookups;
    } */

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
            sourceFiles = MzIdentMLTransformer.transformSourceFile(unmarshaller.getSourceFiles());
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve source files", ex);
        }
        return sourceFiles;
    }

    @Override
    public List<Organization> getOrganizationContacts() throws DataAccessException {
        List<Organization> organizationList = new ArrayList<Organization>();
        try {
            organizationList = MzIdentMLTransformer.transformToOrganization(unmarshaller.getOrganizationContacts());
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve contacts", ex);
        }
        return organizationList;
    }

    @Override
    public List<Person> getPersonContacts() throws DataAccessException {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList= MzIdentMLTransformer.transformToPerson(unmarshaller.getPersonContacts());
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve contacts", ex);
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
                List<Sample> samples = MzIdentMLTransformer.transformToSample(unmarshaller.getSampleList());
                metaData.setSampleList(samples);
                return samples;
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve samples", ex);
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
    public List<Software> getSoftwareList() throws DataAccessException {
        ExperimentMetaData metaData = super.getExperimentMetaData();

        if (metaData == null) {
            try {
                List<Software> softwares = MzIdentMLTransformer.transformToSoftware(unmarshaller.getSoftwares());
                metaData.setSoftwareList(softwares);
                return softwares;
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve software", ex);
            }
        } else {
            return metaData.getSoftwareList();
        }
    }

    /**
     * Get a list of references
     *
     * @return List<Reference>  a list of reference objects
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *
     */
    private List<Reference> getReferences() throws DataAccessException {
        List<Reference> refs = null;
        try {
            refs = MzIdentMLTransformer.transformToReference(unmarshaller.getReferences());
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
    private ExperimentProtocol getProtocol() throws DataAccessException {
        try {
          //  return PrideXmlTransformer.transformProtocol(reader.getProtocol());
        } catch (Exception ex) {
            throw new DataAccessException("Failed to retrieve protocol", ex);
        }
        return null;
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
            try {
               // return PrideXmlTransformer.transformAdditional(reader.getAdditionalParams());
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve additional information", ex);
            }
        }
        return null;
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

                // Get Accession for Pride XML Object
                String accession = unmarshaller.getMzIdentMLId();
                // Get the Version of the Pride File.
                String version = unmarshaller.getMzIdentMLVersion();
                //Get Source File List
                List<SourceFile> sources = getSourceFiles();
                // Get Samples objects for PRide Object
                List<Sample> samples = getSamples();
                // Get all the softwares related with the object
                List<Software> softwares = getSoftwareList();
                // Get Contact Persons
                List<Person> persons = getPersonContacts();
                // Get the Contact Organization
                List<Organization> organizations = getOrganizationContacts();
                // Get Additional Information Related with the Project
                ParamGroup additional = getAdditional();
                // Get the Experiment Title
                //Todo: Florian is in charge of get the name of the file.
                String title = null;
                // Get The Experiment Short Label
                String shortLabel = null;
                //Get Experiment Protocol
                ExperimentProtocol protocol = getProtocol();
                // Get References From the Experiment
                List<Reference> references = getReferences();

                metaData = new ExperimentMetaData(additional,accession,title,version,shortLabel,samples,softwares,persons,sources,null,organizations,references,null,null,protocol);
                // store it in the cache
                cache.store(CacheCategory.EXPERIMENT_METADATA, metaData);
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve meta data", ex);
            }
        }

        return metaData;
    }

    @Override
    public IdentificationMetaData getIdentificationMetaData() throws DataAccessException {
        IdentificationMetaData metaData = super.getIdentificationMetaData();
        if(metaData == null){
            List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList = null;
            //Todo: Try to convert the CVTerms in Pride to SpectrumIdentificationProtocol
            Protocol proteinDetectionProtocol = null;
            //Todo: Try to convert the CVTerms in Pride to Protocol
            List<SearchDataBase> searchDataBaseList = null;
            //Todo: We need to search in the peptides Identifications all of the Search Databases Used.
            //Todo: We need to search all of the possible modifications presented in the experiment.

            metaData = new IdentificationMetaData(null,null,spectrumIdentificationProtocolList,proteinDetectionProtocol,searchDataBaseList);
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
    /*@Override
    Spectrum getSpectrumById(Comparable id, boolean useCache) throws DataAccessException {
        Spectrum spectrum = super.getSpectrumById(id, useCache);
        if (spectrum == null && id != null) {
            logger.debug("Get new spectrum from file: {}", id);
            try {
                spectrum = PrideXmlTransformer.transformSpectrum(reader.getSpectrumById(id.toString()));
                if (useCache && spectrum != null) {
                    cache.store(CacheCategory.SPECTRUM, id, spectrum);
                }
            } catch (Exception ex) {
                throw new DataAccessException("Failed to retrieve spectrum: " + id, ex);
            }
        }
        return spectrum;
    }*/

    /**
     * Check whether the spectrum has been identified.
     *
     * @param specId spectrum id
     * @return boolean     true means identified
     * @throws DataAccessException data access exception
     */
    /*@Override
    public boolean isIdentifiedSpectrum(Comparable specId) throws DataAccessException {
        return reader.isIdentifiedSpectrum(specId.toString());
    } */

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
                ident = MzIdentMLTransformer.transformToIdentification(unmarshaller.getIdentificationById(id), unmarshaller.getFragmentationTable());
                if (useCache && ident != null) {
                    // store identification into cache
                    cache.store(CacheCategory.IDENTIFICATION, id, ident);
                    // store precursor charge and m/z
                    for (Peptide peptide : ident.getIdentifiedPeptides()) {
                        Spectrum spectrum = peptide.getSpectrum();
                        if (spectrum != null) {
                            cache.store(CacheCategory.PRECURSOR_CHARGE, spectrum.getId(), DataAccessUtilities.getPrecursorCharge(spectrum));
                            cache.store(CacheCategory.PRECURSOR_MZ, spectrum.getId(), DataAccessUtilities.getPrecursorMz(spectrum));
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
    public Peptide getPeptideById(Comparable index, boolean useCache) throws DataAccessException {
        Peptide peptide = super.getPeptideById(null, index, useCache);
        if (peptide == null) {
            logger.debug("Get new peptide from file: {}", index);
            peptide = MzIdentMLTransformer.transformToPeptideIdentification(unmarshaller.getPeptideIdentificationById(index),unmarshaller.getFragmentationTable());
            if (useCache && peptide != null) {
                // store peptide
                cache.store(CacheCategory.PEPTIDE, new Tuple<Comparable, Comparable>(identId, index), peptide);
                // store precursor charge and m/z
                Spectrum spectrum = peptide.getSpectrum();
                if (spectrum != null) {
                    cache.store(CacheCategory.PRECURSOR_CHARGE, spectrum.getId(), DataAccessUtilities.getPrecursorCharge(spectrum));
                    cache.store(CacheCategory.PRECURSOR_MZ, spectrum.getId(), DataAccessUtilities.getPrecursorMz(spectrum));
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