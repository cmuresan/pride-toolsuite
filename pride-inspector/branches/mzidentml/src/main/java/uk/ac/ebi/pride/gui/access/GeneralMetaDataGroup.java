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
        return this.getMetaData().getId();
    }

    public String getName() {
        return this.getMetaData().getName();
    }

    public String getShortLabel() {
        return getMetaData().getShortLabel();
    }

    public List<InstrumentConfiguration> getInstrumentConfigurations() {
        return getMzGraphMetaData().getInstrumentConfigurations();
    }

    public List<Reference> getReferences() {
        return getMetaData().getReferences();
    }

    public List<Person> getPersonList() {
        return getMetaData().getPersonList();
    }

    public List<Sample> getSampleList() {
        return getMetaData().getSampleList();
    }

    public ExperimentProtocol getProtocol() {
        return getMetaData().getProtocol();
    }

    public List<DataProcessing> getDataProcessings() {
        return getMzGraphMetaData().getDataProcessingList();
    }

    public List<SearchDataBase> getSearchDatabases() {
        return getIdentificationMetaData().getSearchDataBaseList();
    }
}
