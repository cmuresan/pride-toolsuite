package uk.ac.ebi.pride.data.jaxb.pridexml.adapter;

import uk.ac.ebi.pride.data.io.XmlUnmarshaller;
import uk.ac.ebi.pride.data.jaxb.pridexml.ExperimentType;
import uk.ac.ebi.pride.data.jaxb.pridexml.SpectrumType;
import uk.ac.ebi.pride.data.xxindex.PrideXmlIndexer;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ToDo: use caching here?
 * ToDo: should this class be located to somewhere else in the package?
 * User: rwang
 * Date: 16-Mar-2010
 * Time: 10:37:47
 */
public class SpectrumAdapter extends XmlAdapter<BigInteger, SpectrumType> {

    private PrideXmlIndexer index = null;
    private String expAcc = null;

    public SpectrumAdapter(PrideXmlIndexer index, String expAcc) {
        this.index = index;
        this.expAcc = expAcc;
    }

    @Override
    public SpectrumType unmarshal(BigInteger id) throws Exception {
        String xml = index.getSpectrumXmlString(expAcc, id.toString());
        XmlUnmarshaller um = XmlUnmarshaller.getInstance(ExperimentType.class.getPackage().getName());
        Collection<XmlAdapter> adapters = new ArrayList<XmlAdapter>();
        adapters.add(new SpectrumAdapter(index, expAcc));
        SpectrumType result = um.unawareNSUnmarshall(xml, SpectrumType.class, adapters);
        return result;
    }

    @Override
    public BigInteger marshal(SpectrumType spectrum) throws Exception {
        if (spectrum !=  null) {
            return new BigInteger(spectrum.getId()+"");
        }
        return null;
    }
}
