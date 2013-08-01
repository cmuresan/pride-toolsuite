package uk.ac.ebi.pride.data.io.file;

//~--- non-JDK imports --------------------------------------------------------

import uk.ac.ebi.jmzml.model.mzml.*;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

//~--- JDK imports ------------------------------------------------------------

/**
 * MzMLUnmarshallerHelper provides a list of convenient methods to access mzML files
 * <p/>
 * User: rwang, yperez
 * Date: 17-May-2010
 * Time: 10:37:45
 */
public class MzMLUnmarshallerAdaptor extends MzMLUnmarshaller {


    public MzMLUnmarshallerAdaptor(File mzMLFile) {
        super(mzMLFile);
    }

    public CVList getCVList() {
        CVList cvList = unmarshalFromXpath("/mzML/cvList", CVList.class);
        return cvList;
    }

    public FileDescription getFileDescription() {
        FileDescription fileDescription = unmarshalFromXpath("/mzML/fileDescription", FileDescription.class);
        return fileDescription;
    }

    public ReferenceableParamGroupList getReferenceableParamGroupList() {
        ReferenceableParamGroupList referenceableParamGroupList = unmarshalFromXpath("/mzML/referenceableParamGroupList", ReferenceableParamGroupList.class);
        return referenceableParamGroupList;
    }

    public SampleList getSampleList() {
        SampleList sampleList = unmarshalFromXpath("/mzML/sampleList", SampleList.class);
        return sampleList;
    }

    public SoftwareList getSoftwares() {
        SoftwareList softwareList = unmarshalFromXpath("/mzML/softwareList", SoftwareList.class);
        return softwareList;
    }

    public ScanSettingsList getScanSettingsList() {
        ScanSettingsList scanSettingsList = unmarshalFromXpath("/mzML/scanSettingsList", ScanSettingsList.class);
        return scanSettingsList;
    }

    public InstrumentConfigurationList getInstrumentConfigurationList() {
        InstrumentConfigurationList instrumentConfigurationList = unmarshalFromXpath("/mzML/instrumentConfigurationList", InstrumentConfigurationList.class);
        return instrumentConfigurationList;
    }

    public DataProcessingList getDataProcessingList() {
        DataProcessingList dataProcessingList = unmarshalFromXpath("/mzML/dataProcessingList", DataProcessingList.class);
        return dataProcessingList;
    }

    public Set<String> getSpectrumIds() {
        return getSpectrumIDs();
    }

    public Set<String> getChromatogramIds() {
        return getChromatogramIDs();
    }

    public Date getCreationDate() {
        Map<String, String> runAttributes = getSingleElementAttributes("/mzML/run");
        String startTimeStamp = runAttributes.get("startTimeStamp");
        Date dateCreation = null;

        if (startTimeStamp != null) {
            Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(startTimeStamp);
            dateCreation = calendar.getTime();
        }
        return dateCreation;
    }
}



