package uk.ac.ebi.pride.data.controller.impl;

import uk.ac.ebi.jmzml.model.mzml.*;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.DataProcessing;
import uk.ac.ebi.pride.data.core.FileDescription;
import uk.ac.ebi.pride.data.core.InstrumentConfiguration;
import uk.ac.ebi.pride.data.core.ParamGroup;
import uk.ac.ebi.pride.data.core.ReferenceableParamGroup;
import uk.ac.ebi.pride.data.core.Sample;
import uk.ac.ebi.pride.data.core.Software;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.data.io.MzMLUnmarshallerAdaptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * MzMLControllerImpl provides methods to access mzML files.
 * 
 * User: rwang
 * Date: 02-Feb-2010
 * Time: 12:31:43
 */
public class MzMLControllerImpl extends AbstractDataAccessController {

    private MzMLUnmarshallerAdaptor unmarshaller = null;

    public MzMLControllerImpl(File file) throws DataAccessException{
        super(file);
        initialize(file);
    }

    private void initialize(File file) throws DataAccessException{
        // create unmarshaller
        MzMLUnmarshaller um = new MzMLUnmarshaller(file);
        unmarshaller = new MzMLUnmarshallerAdaptor(um);
        
        // set data source name
        this.setName(file.getName());
            
        List<Comparable> spectrumIds = this.getSpectrumIds();
        if (spectrumIds != null && !spectrumIds.isEmpty()) {
            this.setForegroundSpectrumById(spectrumIds.get(0));
        }

        List<Comparable> chromaIds = this.getChromatogramIds();
        if (chromaIds != null && !chromaIds.isEmpty()) {
           this.setForegroundChromatogramById(chromaIds.get(0));
        }
    }

    @Override
    public void close() {
        //ToDo: implement
    }

    public MetaData getMetaData() throws DataAccessException {
        // id , accession and version
        String id = unmarshaller.getMzMLId();
        String accession = unmarshaller.getMzMLAccession();
        String version = unmarshaller.getMzMLVersion();
        // FileDescription
        FileDescription fileDesc = getFileDescription();
        // Sample list
        List<Sample> samples = getSamples();
        // Software list
        List<Software> softwares = getSoftware();
        // ScanSettings list
        List<ScanSetting> scanSettings = getScanSettings();
        // Instrument configuration
        List<InstrumentConfiguration> instrumentConfigurations = getInstrumentConfigurations();
        // Data processing list
        List<DataProcessing> dataProcessings = getDataProcessings();
        // Param group
        ParamGroup params =  null;

        return new MetaData(id, accession, version, fileDesc,
                            samples, softwares, scanSettings, instrumentConfigurations,
                            dataProcessings, params);
    }

    public List<CVLookup> getCvLookups() throws DataAccessException {
        try {
             CVList rawCvList = unmarshaller.getCVList();
             return MzMLTransformer.transformCVList(rawCvList);
        } catch(MzMLUnmarshallerException e){
            throw new DataAccessException("Exception while trying to read a list of cv lookups", e);
        }
    }

    @Override
    public FileDescription getFileDescription() throws DataAccessException {
        try {
             uk.ac.ebi.jmzml.model.mzml.FileDescription
                     rawFileDesc = unmarshaller.getFileDescription();
             return MzMLTransformer.transformFileDescription(rawFileDesc);
        } catch(MzMLUnmarshallerException e){
            throw new DataAccessException("Exception while trying to read file description", e);
        }
    }

    @Override
    public ReferenceableParamGroup getReferenceableParamGroup() throws DataAccessException {
        try {
             ReferenceableParamGroupList rawRefParamGroup = unmarshaller.getReferenceableParamGroupList();
             return MzMLTransformer.transformReferenceableParamGroupList(rawRefParamGroup);
        } catch(MzMLUnmarshallerException e){
            throw new DataAccessException("Exception while trying to read referenceable param group", e);
        }
    }

    @Override
    public List<Sample> getSamples() throws DataAccessException {
        try {
             SampleList rawSample = unmarshaller.getSampleList();
             return MzMLTransformer.transformSampleList(rawSample);
        } catch(MzMLUnmarshallerException e){
            throw new DataAccessException("Exception while trying to read smaples", e);
        }
    }

    @Override
    public List<Software> getSoftware() throws DataAccessException {
        try {
             SoftwareList rawSoftware = unmarshaller.getSoftwareList();
             return MzMLTransformer.transformSoftwareList(rawSoftware);
        } catch(MzMLUnmarshallerException e){
            throw new DataAccessException("Exception while trying to read softwares", e);
        }
    }

    @Override
    public List<ScanSetting> getScanSettings() throws DataAccessException {
        try {
             ScanSettingsList rawScanSettingList = unmarshaller.getScanSettingsList();
             return MzMLTransformer.transformScanSettingList(rawScanSettingList);
        } catch(MzMLUnmarshallerException e){
            throw new DataAccessException("Exception while trying to read scan settings list", e);
        }
    }

    @Override
    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        try {
             InstrumentConfigurationList rawInstrumentList = unmarshaller.getInstrumentConfigurationList();
             return MzMLTransformer.transformInstrumentConfigurationList(rawInstrumentList);
        } catch(MzMLUnmarshallerException e){
            throw new DataAccessException("Exception while trying to read instrument configuration list", e);
        }
    }

    @Override
    public List<DataProcessing> getDataProcessings() throws DataAccessException {
        try {
             uk.ac.ebi.jmzml.model.mzml.DataProcessingList
                     rawDataProcList = unmarshaller.getDataProcessingList();
             return MzMLTransformer.transformDataProcessingList(rawDataProcList);
        } catch(MzMLUnmarshallerException e){
            throw new DataAccessException("Exception while trying to read data processing list", e);
        }
    }

    @Override
    public List<Comparable> getSpectrumIds() throws DataAccessException {
        return new ArrayList<Comparable>(unmarshaller.getSpectrumIds());
    }


    @Override
    public Spectrum getSpectrumById(Comparable id) throws DataAccessException {
        try {
            uk.ac.ebi.jmzml.model.mzml.Spectrum
                    rawSpec = unmarshaller.getSpectrumById(id.toString());
            return MzMLTransformer.transformSpectrum(rawSpec);
        } catch(MzMLUnmarshallerException ex) {
            throw new DataAccessException("Exception while trying to read Spectrum using Spectrum ID", ex);
        }
    }

    @Override
    public List<Comparable> getChromatogramIds() throws DataAccessException {
        return new ArrayList<Comparable>(unmarshaller.getChromatogramIds());
    }

    @Override
    public Chromatogram getChromatogramById(Comparable id) throws DataAccessException {
        try {
            uk.ac.ebi.jmzml.model.mzml.Chromatogram
                    rawChroma = unmarshaller.getChromatogramById(id.toString());
            return MzMLTransformer.transformChromatogram(rawChroma);
        } catch(MzMLUnmarshallerException ex) {
            throw new DataAccessException("Exception while trying to read Chromatogram using chromatogram ID", ex);
        }
    }
}
