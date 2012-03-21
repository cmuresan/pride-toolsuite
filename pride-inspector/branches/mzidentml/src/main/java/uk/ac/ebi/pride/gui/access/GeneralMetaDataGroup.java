package uk.ac.ebi.pride.gui.access;

import uk.ac.ebi.pride.data.core.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 2/1/12
 * Time: 5:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralMetaDataGroup {

    IdentificationMetaData identificationMetaData = null;

    ExperimentMetaData metaData = null;

    MzGraphMetaData mzGraphMetaData = null;

    public GeneralMetaDataGroup(IdentificationMetaData identificationMetaData, ExperimentMetaData metaData, MzGraphMetaData mzGraphMetaData) {
        this.identificationMetaData = identificationMetaData;
        this.metaData = metaData;
        this.mzGraphMetaData = mzGraphMetaData;
    }

    public IdentificationMetaData getIdentificationMetaData() {
        return identificationMetaData;
    }

    public void setIdentificationMetaData(IdentificationMetaData identificationMetaData) {
        this.identificationMetaData = identificationMetaData;
    }

    public ExperimentMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(ExperimentMetaData metaData) {
        this.metaData = metaData;
    }

    public MzGraphMetaData getMzGraphMetaData() {
        return mzGraphMetaData;
    }

    public void setMzGraphMetaData(MzGraphMetaData mzGraphMetaData) {
        this.mzGraphMetaData = mzGraphMetaData;
    }

    public Object getId() {
        if(getMetaData() == null){
            return null;
        }
        return this.getMetaData().getId();
    }

    public String getName() {
        if(getMetaData() == null){
            return null;
        }
        return this.getMetaData().getName();
    }

    public String getShortLabel() {
        if(getMetaData() == null){
            return null;
        }
        return getMetaData().getShortLabel();
    }

    public List<InstrumentConfiguration> getInstrumentConfigurations() {
        if(hasMzGraphMetadata()){
            return getMzGraphMetaData().getInstrumentConfigurations();
        }
        return null;
    }

    public List<Reference> getReferences() {
        if(getMetaData() == null){
            return null;
        }
        return getMetaData().getReferences();
    }

    public List<Person> getPersonList() {
        if(getMetaData() == null){
            return null;
        }
        return getMetaData().getPersonList();
    }

    public List<Sample> getSampleList() {
        if(getMetaData() == null){
            return null;
        }
        return getMetaData().getSampleList();
    }

    public ExperimentProtocol getProtocol() {
        if(getMetaData() == null){
            return null;
        }
        return getMetaData().getProtocol();
    }

    public List<DataProcessing> getDataProcessings() {
        if(getMzGraphMetaData() == null){
            return null;
        }
        return getMzGraphMetaData().getDataProcessingList();
    }

    public List<SearchDataBase> getSearchDatabases() {
        if(getIdentificationMetaData() == null){
            return null;
        }
        return getIdentificationMetaData().getSearchDataBaseList();
    }

    public List<SpectrumIdentificationProtocol> getSpectrumIdentificationProtocol() {
        if(getIdentificationMetaData() == null){
            return null;
        }
        return getIdentificationMetaData().getSpectrumIdentificationProtocolList();
    }

    public Protocol getProteinDetectionProtocol() {
        if(getIdentificationMetaData() == null){
            return null;
        }
        return this.getIdentificationMetaData().getProteinDetectionProtocol();
    }

    public boolean hasIdentificationMetadata(){
        if(getIdentificationMetaData() == null){
            return false;
        }
        return true;
    }

    public boolean hasMzGraphMetadata(){
        if(getMzGraphMetaData() == null){
            return false;
        }
        return true;

    }

    public boolean hasSampleProtocolMetadata() {
        if(metaData.getSampleList() !=null || metaData.getProtocol() != null){
            return true;
        }
        return false;
    }
}
