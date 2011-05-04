package uk.ac.ebi.pride.data.controller.impl;

import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 02-Feb-2010
 * Time: 12:31:30
 */
public class PrideXmlControllerImpl extends AbstractDataAccessController {

    private PrideXmlReader reader = null;
    private PrideXmlTransformer transformer = null;

    public PrideXmlControllerImpl(File file) throws DataAccessException {
        super(file);
        initialize(file);
    }

    private void initialize(File file) throws DataAccessException {
        // create pride access utils
        reader = new PrideXmlReader(file);
        // set data source description
        this.setName(file.getName());

        List<Comparable> spectrumIds = this.getSpectrumIds();
        // create pride xml transformer
        transformer = new PrideXmlTransformer(spectrumIds);
        if (spectrumIds != null && !spectrumIds.isEmpty()) {
            this.setForegroundSpectrumById(spectrumIds.get(0));
        }

        List<Comparable> tdIdentIds = this.getTwoDimIdentIds();
        if (tdIdentIds != null && !tdIdentIds.isEmpty()) {
            this.setForegroundTwoDimIdentById(tdIdentIds.get(0));
        }

        List<Comparable> gfIdentIds = this.getGelFreeIdentIds();
        if (gfIdentIds != null && !gfIdentIds.isEmpty()) {
            this.setForegroundGelFreeIdentById(gfIdentIds.get(0));
        }
    }

    /**
     * Get a list of cv lookup objects.
     * @return List<CVLookup>   a list of cvlookup objects.
     * @throws DataAccessException
     */
    @Override
    public List<CVLookup> getCvLookups() throws DataAccessException {
        return  transformer.transformCvLookups(reader.getCvLookups());
    }


    /**
     * Get the FileDescription object
     * @return FileDescription  FileDescription object.
     * @throws DataAccessException
     */
    @Override
    public FileDescription getFileDescription() throws DataAccessException {
        ParamGroup fileContent = transformer.transformFileContent();
        List<SourceFile> sourceFiles = getSourceFiles();
        List<ParamGroup> contacts = getContacts();
        return new FileDescription(fileContent, sourceFiles, contacts);
    }

    /**
     * Get a list of source files.
     * @return List<SourceFile> a list of source file objects.
     */
    public List<SourceFile> getSourceFiles() {
        List<SourceFile> sourceFiles = new ArrayList<SourceFile>();
        SourceFile sourceFile = transformer.transformSourceFile(reader.getAdmin());
        if (sourceFile != null) {
            sourceFiles.add(sourceFile);
        }
        return sourceFiles;
    }

    /**
     * Get a list of contact details
     * @return List<ParamGroup> a list of parameter groups.
     */
    public List<ParamGroup> getContacts() {
        return transformer.transformContacts(reader.getAdmin());
    }

    /**
     * Get a list of samples
     * @return List<Sample> a list of sample objects.
     * @throws DataAccessException
     */
    @Override
    public List<Sample> getSamples() throws DataAccessException {
        List<Sample> samples = new ArrayList<Sample>();
        Sample sample = transformer.transformSample(reader.getAdmin());
        if (sample != null) {
            samples.add(sample);
        }
        return samples;
    }

    /**
     * Get a list of software
     * @return List<Software>   a list of software objects.
     * @throws DataAccessException
     */
    @Override
    public List<Software> getSoftware() throws DataAccessException {
        List<Software> softwares = new ArrayList<Software>();
        Software software = transformer.transformSoftware(reader.getDataProcessing());
        if (software != null) {
            softwares.add(software);
        }
        return softwares;
    }

    /**
     * Get a list of instruments
     * @return List<Instrument> a list of instruments.
     * @throws DataAccessException
     */
    @Override
    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        return transformer.transformInstrument(reader.getInstrument(), reader.getDataProcessing());
    }

    /**
     * Get a list of data processing objects
     * @return List<DataProcessing> a list of data processing objects
     * @throws DataAccessException
     */
    @Override
    public List<DataProcessing> getDataProcessings() throws DataAccessException {
        List<DataProcessing> dataProcessings = new ArrayList<DataProcessing>();
        DataProcessing dataProcessing = transformer.transformDataProcessing(reader.getDataProcessing());
        if (dataProcessing != null) {
            dataProcessings.add(dataProcessing);
        }
        return dataProcessings;
    }

    /**
     * Get a list of references
     * @return List<Reference>  a list of reference objects
     */
    private List<Reference> getReferences() {
        return transformer.transformReferences(reader.getReferences());
    }

    /**
     * Get the protocol object
     * @return Protocol protocol object.
     */
    private Protocol getProtocol() {
        return transformer.transformProtocol(reader.getProtocol());
    }

    /**
     * Get additional parameters
     * @return ParamGroup   a group of cv parameters and user parameters.
     */
    private ParamGroup getAdditional() {
        return transformer.transformAdditional(reader.getAdditionalParams());
    }

    @Override
    public MetaData getMetaData() throws DataAccessException {
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
        return new Experiment(null, accession, version, fileDesc,
                samples, software, null, instrumentConfigurations,
                dataProcessings, additional, title, shortLabel,
                protocol, references, null, null);
    }

    @Override
    public List<Comparable> getSpectrumIds() throws DataAccessException {
        return new ArrayList<Comparable>(reader.getSpectrumIds());
    }

    @Override
    public Spectrum getSpectrumById(Comparable id) throws DataAccessException {
        return transformer.transformSpectrum(reader.getSpectrumById(id.toString()));
    }

    @Override
    public List<Comparable> getTwoDimIdentIds() throws DataAccessException {
        return new ArrayList<Comparable>(reader.getTwoDimIdentIds());
    }

    @Override
    public TwoDimIdentification getTwoDimIdentById(Comparable id) throws DataAccessException {
        return transformer.transformTwoDimIdent(reader.getTwoDimIdentById(id.toString()));
    }

    @Override
    public List<Comparable> getGelFreeIdentIds() throws DataAccessException {
        return new ArrayList<Comparable>(reader.getGelFreeIdentIds());
    }

    @Override
    public GelFreeIdentification getGelFreeIdentById(Comparable id) throws DataAccessException {
        return transformer.transformGelFreeIdent(reader.getGelFreeIdentById(id.toString()));
    }

    @Override
    public void close() {
        //ToDo: add a close method for PrideXmlReader
    }
}
