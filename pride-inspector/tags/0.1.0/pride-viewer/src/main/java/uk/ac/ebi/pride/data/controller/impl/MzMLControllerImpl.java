package uk.ac.ebi.pride.data.controller.impl;

import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ToDo: add call back after creating the index for Experiment, is this really necessary? 
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 02-Feb-2010
 * Time: 12:31:43
 */
public class MzMLControllerImpl extends AbstractDataAccessController {
    private MzMLUnmarshaller unmarshaller = null;

    public MzMLControllerImpl(File file) throws DataAccessException{
        unmarshaller = new MzMLUnmarshaller(file);
        this.setName(file.getName());
        this.setExperimentFriendly(false);
        this.setSpectrumFriendly(true);
        this.setIdentificationFriendly(false);
        this.setSource(file);
        initialize();
    }

    private void initialize() throws DataAccessException{
        List<String> spectrumIds = this.getSpectrumIds();
        if (spectrumIds != null && !spectrumIds.isEmpty()) {
            foregroundSpectrum = this.getSpectrumById(spectrumIds.get(0));
        }
        
        List<String> chromaIds = this.getChromatogramIds();
        if (chromaIds != null && !chromaIds.isEmpty()) {
           foregroundChromatogram = this.getChromatogramById(chromaIds.get(0));
        }
    }

    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public MzML getMzMLMetaData() throws DataAccessException {
        MzML mzML = null;
        try {
            // cv list
            uk.ac.ebi.jmzml.model.mzml.CVList rawCvList = unmarshaller.getCVList();
            List<CVLookup> cvLookups = MzMLTransformer.transformCVList(rawCvList);
            // FileDescription
            uk.ac.ebi.jmzml.model.mzml.FileDescription rawFileDesc = unmarshaller.getFileDescription();
            ParamGroup fileContent = MzMLTransformer.transformParamGroup(rawFileDesc.getFileContent());
            List<SourceFile> sourceFiles = MzMLTransformer.transformSourceFileList(rawFileDesc.getSourceFileList());
            List<ParamGroup> contacts = MzMLTransformer.transformParamGroupList(rawFileDesc.getContact());
            // Referenceable Param group
            uk.ac.ebi.jmzml.model.mzml.ReferenceableParamGroupList rawRefParamGroup = unmarshaller.getReferenceableParamGroupList();
            Map<String, ParamGroup> refParamGroup = MzMLTransformer.transformReferenceableParamGroupList(rawRefParamGroup);
            // Sample list
            uk.ac.ebi.jmzml.model.mzml.SampleList sampleList = unmarshaller.getSampleList();
            List<Sample> samples = MzMLTransformer.transformSampleList(sampleList);
            // Software list
            uk.ac.ebi.jmzml.model.mzml.SoftwareList softwareList = unmarshaller.getSoftwareList();
            List<Software> softwares = MzMLTransformer.transformSoftwareList(softwareList);

            // ScanSettings list
            uk.ac.ebi.jmzml.model.mzml.ScanSettingsList rawScanSettingsList = unmarshaller.getScanSettingsList();
            List<ScanSetting> scanSettings = MzMLTransformer.transformScanSettingList(rawScanSettingsList);
            // Instrument configuration
            uk.ac.ebi.jmzml.model.mzml.InstrumentConfigurationList rawInstrumentList = unmarshaller.getInstrumentConfigurationList();
            List<Instrument> instruments = MzMLTransformer.transformInstrumentConfigurationList(rawInstrumentList);
            // Data processing list
            uk.ac.ebi.jmzml.model.mzml.DataProcessingList rawDataProcessingList = unmarshaller.getDataProcessingList();
            List<DataProcessing> dataProcessings = MzMLTransformer.transformDataProcessingList(rawDataProcessingList);

            mzML = new MzML(null, null, null, cvLookups, fileContent, sourceFiles, contacts, refParamGroup,
                            samples, softwares, scanSettings, instruments, dataProcessings);
        } catch(MzMLUnmarshallerException ex) {
            throw new DataAccessException("Exception while trying to read MzML meta data", ex);
        }
        return mzML;
    }

    @Override
    public List<String> getSpectrumIds() throws DataAccessException {
        return new ArrayList<String>(unmarshaller.getSpectrumIDs());
    }


    @Override
    public Spectrum getSpectrumById(String id) throws DataAccessException {
        uk.ac.ebi.jmzml.model.mzml.Spectrum rawSpec = null;
        Spectrum spec = null;
        try {
            rawSpec = unmarshaller.getSpectrumById(id);
            if (rawSpec != null)
                spec = MzMLTransformer.transformSpectrum(rawSpec);
        } catch(MzMLUnmarshallerException ex) {
            throw new DataAccessException("Exception while trying to read Spectrum using Spectrum ID", ex);
        }
        return spec;
    }

    @Override
    public List<String> getChromatogramIds() throws DataAccessException {
        return new ArrayList<String>(unmarshaller.getChromatogramIDs());
    }

    @Override
    public Chromatogram getChromatogramById(String id) throws DataAccessException {
        uk.ac.ebi.jmzml.model.mzml.Chromatogram rawChroma = null;
        Chromatogram chroma = null;
        try {
            rawChroma = unmarshaller.getChromatogramById(id);
            if (rawChroma != null)
               chroma = MzMLTransformer.transformChromatogram(rawChroma);
        } catch(MzMLUnmarshallerException ex) {
            throw new DataAccessException("Exception while trying to read Chromatogram using chromatogram ID", ex);
        }
        return chroma;
    }
}
