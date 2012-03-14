package uk.ac.ebi.pride.data.io.file;



import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLFile;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLParsingException;
import uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 2/27/12
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MzXmlUnmarshallerAdaptor {

    private MzXMLFile unmarshaller = null;

    public MzXmlUnmarshallerAdaptor(MzXMLFile um) {
        this.unmarshaller = um;
    }

    public List<String> getSpectrumIds() {
        return unmarshaller.getSpectraIds();
    }

    public Spectrum getSpectrumById(String id) throws JMzReaderException {
        return unmarshaller.getSpectrumById(id);
    }

    public List<Operator> getPersonContacts() throws MzXMLParsingException {
        List<Operator> operators = null;
        List<MsInstrument> msInstruments = unmarshaller.getMsInstrument();
        if(msInstruments != null && msInstruments.size() != 0){
            operators = new ArrayList<Operator>();
            for (MsInstrument msInstrument: msInstruments){
                operators.add(msInstrument.getOperator());
            }
        }
        return operators;
    }

    public List<Software> getSoftwares() throws MzXMLParsingException {
        List<Software> softwares = null;
        List<MsInstrument> msInstruments     = unmarshaller.getMsInstrument();
        List<DataProcessing> dataProcessings = unmarshaller.getDataProcessing();
        if((msInstruments != null && msInstruments.size() !=0) || (dataProcessings != null && dataProcessings.size() !=0)){
            softwares = new ArrayList<Software>();
            for(MsInstrument msInstrument: msInstruments){
                softwares.add(msInstrument.getSoftware());
            }
            for(DataProcessing dataProcessing: dataProcessings){
                softwares.add(dataProcessing.getSoftware());
            }
        }
        return softwares;
    }

    public List<ParentFile> getParentFiles() throws MzXMLParsingException {
        return unmarshaller.getParentFile();
    }

    public List<MsInstrument> getMsInstruments() throws MzXMLParsingException {
        return unmarshaller.getMsInstrument();
    }




}