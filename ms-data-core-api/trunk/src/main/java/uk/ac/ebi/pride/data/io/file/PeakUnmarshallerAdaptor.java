package uk.ac.ebi.pride.data.io.file;

import uk.ac.ebi.pride.tools.jmzreader.JMzReader;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 3/15/12
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeakUnmarshallerAdaptor {

    private JMzReader unmarshaller = null;

    public PeakUnmarshallerAdaptor(JMzReader um) {
        this.unmarshaller = um;
    }

    public List<String> getSpectrumIds() {
        return unmarshaller.getSpectraIds();
    }

    public Spectrum getSpectrumById(String id) throws JMzReaderException{
        return unmarshaller.getSpectrumById(id);
    }


}
