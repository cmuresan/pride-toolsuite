package uk.ac.ebi.pride.data.io;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.jaxb.pridexml.*;
import uk.ac.ebi.pride.data.jaxb.pridexml.adapter.SpectrumAdapter;
import uk.ac.ebi.pride.data.utils.PatternUtils;
import uk.ac.ebi.pride.data.xxindex.PrideXmlIndexer;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This unmarshaller translate xml string into JAXB objects
 * User: rwang
 * Date: 16-Mar-2010
 * Time: 10:52:15
 */
public class PrideXmlUnmarshaller {
    private static final Logger logger = Logger.getLogger(PrideXmlUnmarshaller.class.getName());
    private Pattern VALUE_PATTERN = Pattern.compile("\\s*<[^>]*>\\s*([^<]*)\\s*<[^>]*>\\s*");

    private File prideXmlFile = null;
    private PrideXmlIndexer index = null;

    public PrideXmlUnmarshaller(File prideXml) {
        this.prideXmlFile = prideXml;
        this.index = new PrideXmlIndexer(prideXmlFile);
    }

    public List<String> getExperimentIds() {
        return index.getExperimentIds();
    }

    public String getExperimentTitle(String expAcc) {
        String title = null;
        try {
            String xmlStr = index.getTitleXmlString(expAcc);
            title = PatternUtils.getMatchedString(VALUE_PATTERN, xmlStr, 1);
        } catch(IOException ioe) {
            logger.error("Error while retrieving experiment title", ioe);
            throw new IllegalStateException("Couldn't read experiment title from xml file");
        }
        return title;
    }

    public List<ReferenceType> getReferences(String expAcc) {
        List<ReferenceType> refs = null;
        try {
            List<String> xmlStrList = index.getReferenceXmlStrings(expAcc);
            if (xmlStrList != null && !xmlStrList.isEmpty()) {
                refs = new ArrayList<ReferenceType>();
                for(String xmlStr : xmlStrList) {
                    ReferenceType ref = unmarshall(xmlStr, ReferenceType.class, expAcc);
                    refs.add(ref);
                }
            }
        } catch(IOException ioe) {
            logger.error("Error while retrieving experiment title", ioe);
            throw new IllegalStateException("Couldn't read experiment title from xml file");
        }
        return refs;
    }

    public String getShortLabel(String expAcc) {
        String sl = null;
        try {
            String xmlStr = index.getShortLabelXmlString(expAcc);
            sl = PatternUtils.getMatchedString(VALUE_PATTERN, xmlStr, 1);
        } catch(IOException ioe) {
            logger.error("Error while retrieving experiment short label", ioe);
            throw new IllegalStateException("Couldn't read experiment short label from xml file");
        }
        return sl;
    }

    public ExperimentType.Protocol getProtocol(String expAcc) {
        ExperimentType.Protocol prot = null;

        try {
            String xmlStr = index.getProtocolXmlString(expAcc);
            prot = unmarshall(xmlStr, ExperimentType.Protocol.class, expAcc);
        } catch(IOException ioe) {
            logger.error("Error while retrieving experiment protocol", ioe);
            throw new IllegalStateException("Couldn't read experiment protocol from xml file");    
        }

        return prot;
    }

    public ParamType getAdditionalParam(String expAcc) {
        ParamType additional = null;

        try {
            String xmlStr = index.getAdditionalParamXmlString(expAcc);
            additional = unmarshall(xmlStr, ParamType.class, expAcc);
        } catch(IOException ioe) {
            logger.error("Error while retrieving experiment protocol", ioe);
            throw new IllegalStateException("Couldn't read experiment protocol from xml file");    
        }

        return additional;
    }

    public AdminType getAdmin(String expAcc) {
        AdminType admin = null;
        try {
            String xmlStr = index.getAdminXmlString(expAcc);
            admin = unmarshall(xmlStr, AdminType.class, expAcc);
        } catch(IOException ioe) {
            logger.error("Error while retrieving experiment admin", ioe);
            throw new IllegalStateException("Couldn't read experiment admin from xml file");        
        }

        return admin;
    }

    public InstrumentDescriptionType getInstrument(String expAcc) {
        InstrumentDescriptionType instrument = null;

        try {
            String xmlStr = index.getInstrumentXmlString(expAcc);
            instrument = unmarshall(xmlStr, InstrumentDescriptionType.class, expAcc);
        } catch(IOException ioe) {
            logger.error("Error while retrieving experiment instrument", ioe);
            throw new IllegalStateException("Couldn't read experiment instrument from xml file");
        }
        
        return instrument;
    }

    public DataProcessingType getDataProcessing(String expAcc) {
        DataProcessingType dataProc = null;

        try {
            String xmlStr = index.getDataProcessingXmlString(expAcc);
            dataProc = unmarshall(xmlStr, DataProcessingType.class, expAcc);
        } catch(IOException ioe) {
            logger.error("Error while retrieving experiment data processing", ioe);
            throw new IllegalStateException("Couldn't read experiment data processing from xml file");
        }

        return dataProc;
    }

    public List<String> getSpectrumIds(String expAcc) {
        return index.getSpectrumIds(expAcc);
    }

    public SpectrumType getSpectrumById(String expAcc, String id) {
        SpectrumType spectrum = null;

        try {
            String xmlStr = index.getSpectrumXmlString(expAcc, id);
            spectrum = unmarshall(xmlStr, SpectrumType.class, expAcc);
        } catch(IOException ioe) {
            logger.error("Error while retrieving spectrumtype by id", ioe);
            throw new IllegalStateException("Couldn't read spectrum from xml file");
        }
        return spectrum;
    }

    public List<String> getGelFreeIdentAccs(String expAcc) {
        return index.getGelFreeIdentAccs(expAcc);
    }

    public GelFreeIdentificationType getGelFeeIdentByAcc(String expAcc, String acc) {
        GelFreeIdentificationType ident = null;

        try {
            String xmlStr = index.getGelFreeIdentXmlString(expAcc, acc);
            ident = unmarshall(xmlStr, GelFreeIdentificationType.class, expAcc);
        } catch(IOException ioe) {
            logger.error("Error while retrieving GelFreeIdentificationType by accession", ioe);
            throw new IllegalStateException("Couldn't read GelFreeIdentificationType from xml file");
        }
        return ident;
    }

    public List<String> getTwoDimIdentAccs(String expAcc) {
        return index.getTwoDimIdentAccs(expAcc);
    }

    public TwoDimensionalIdentificationType getTwoDimIdentByAcc(String expAcc, String acc) {
        TwoDimensionalIdentificationType ident = null;

        try {
            String xmlStr = index.getTwoDimIdentXmlString(expAcc, acc);
            ident = unmarshall(xmlStr, TwoDimensionalIdentificationType.class, expAcc);
        } catch(IOException ioe) {
            logger.error("Error while retrieving TwoDimensionalIdentificationType by accession", ioe);
            throw new IllegalStateException("Couldn't read TwoDimensionalIdentificationType from xml file");
        }
        return ident;
    }

    private <T extends Object> T unmarshall(String xml, Class<T> className, String expAcc) {
        T result = null;
        XmlUnmarshaller um = XmlUnmarshaller.getInstance(ExperimentType.class.getPackage().getName());
        Collection<XmlAdapter> adapters = new ArrayList<XmlAdapter>();
        adapters.add(new SpectrumAdapter(index, expAcc));
        if (xml != null) {
            result = um.unawareNSUnmarshall(xml, className, adapters);
        }
        return result;
    }
}
