package uk.ac.ebi.pride.data.io.file;

//~--- non-JDK imports --------------------------------------------------------

import uk.ac.ebi.jmzml.model.mzml.*;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
import uk.ac.ebi.pride.data.core.CvParam;
import uk.ac.ebi.pride.term.CvTermReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

//~--- JDK imports ------------------------------------------------------------

/**
 * MzMLUnmarshallerHelper provides a list of convenient methods to access mzML files
 * <p/>
 * User: rwang
 * Date: 17-May-2010
 * Time: 10:37:45
 */
public class MzMLUnmarshallerAdaptor {

    private MzMLUnmarshaller unmarshaller = null;

    public MzMLUnmarshallerAdaptor(MzMLUnmarshaller um) {
        this.unmarshaller = um;
    }

    public String getMzMLId() {
        return unmarshaller.getMzMLId();
    }

    public String getMzMLAccession() {
        return unmarshaller.getMzMLAccession();
    }

    public String getMzMLVersion() {
        return unmarshaller.getMzMLVersion();
    }

    public CVList getCVList() throws MzMLUnmarshallerException {
        return unmarshaller.unmarshalFromXpath("/mzML/cvList", CVList.class);
    }

    public FileDescription getFileDescription() throws MzMLUnmarshallerException {
        return unmarshaller.unmarshalFromXpath("/mzML/fileDescription", FileDescription.class);
    }

    public ReferenceableParamGroupList getReferenceableParamGroupList() throws MzMLUnmarshallerException {
        return unmarshaller.unmarshalFromXpath("/mzML/referenceableParamGroupList", ReferenceableParamGroupList.class);
    }

    public SampleList getSampleList() throws MzMLUnmarshallerException {
        return unmarshaller.unmarshalFromXpath("/mzML/sampleList", SampleList.class);
    }

    public SoftwareList getSoftwares() throws MzMLUnmarshallerException {
        return unmarshaller.unmarshalFromXpath("/mzML/softwareList", SoftwareList.class);
    }

    public ScanSettingsList getScanSettingsList() throws MzMLUnmarshallerException {
        return unmarshaller.unmarshalFromXpath("/mzML/scanSettingsList", ScanSettingsList.class);
    }

    public InstrumentConfigurationList getInstrumentConfigurationList() throws MzMLUnmarshallerException {
        return unmarshaller.unmarshalFromXpath("/mzML/instrumentConfigurationList", InstrumentConfigurationList.class);
    }

    public DataProcessingList getDataProcessingList() throws MzMLUnmarshallerException {
        return unmarshaller.unmarshalFromXpath("/mzML/dataProcessingList", DataProcessingList.class);
    }

    public Set<String> getSpectrumIds() {
        return unmarshaller.getSpectrumIDs();
    }

    public Set<String> getChromatogramIds() {
        return unmarshaller.getChromatogramIDs();
    }

    public Spectrum getSpectrumById(String id) throws MzMLUnmarshallerException {
        return unmarshaller.getSpectrumById(id);
    }

    public Chromatogram getChromatogramById(String id) throws MzMLUnmarshallerException {
        return unmarshaller.getChromatogramById(id);
    }

    public Date getCreationDate() {
        Run run = unmarshaller.unmarshalFromXpath("/mzML/run",Run.class);

        /*
         * This is the only way that we can use now to retrieve the name property
         * In the future we need to think in more elaborated way.
         */
        Date dateCreation = null;
        run.getStartTimeStamp();
        if(run.getStartTimeStamp() != null){
            dateCreation = run.getStartTimeStamp().getTime();
        }
        return dateCreation;
    }
}



