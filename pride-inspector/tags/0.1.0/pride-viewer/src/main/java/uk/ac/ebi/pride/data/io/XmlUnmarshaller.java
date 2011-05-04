package uk.ac.ebi.pride.data.io;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rwang
 * Date: 16-Mar-2010
 * Time: 16:59:53
 */
public class XmlUnmarshaller {
    private static final Logger logger = Logger.getLogger(XmlUnmarshaller.class.getName());

    private JAXBContext jaxbContext = null;

    private static Map<String, XmlUnmarshaller> unmarshallerMap = new HashMap<String, XmlUnmarshaller>();


    private XmlUnmarshaller(String packageName) {
        try {
            jaxbContext = JAXBContext.newInstance(packageName);
        } catch(JAXBException e){
            logger.error("Error while initializing XmlUnmarshaller", e);
            throw new IllegalStateException("Couldn't initialize XmlUnmarshaller");
        }
    }

    public static XmlUnmarshaller getInstance(String packageName) {
        XmlUnmarshaller unmarshaller = unmarshallerMap.get(packageName);
        if (unmarshaller == null) {
            unmarshaller = new XmlUnmarshaller(packageName);
            unmarshallerMap.put(packageName, unmarshaller);
        }
        return unmarshaller;
    }

    public <T extends Object> T unawareNSUnmarshall(String xml, Class<T> className, Collection<XmlAdapter> adapters) {
        T result = null;

        try {
            // name space aware to false
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(false);
            // create unmarshaller
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            if (adapters != null && !adapters.isEmpty()) {
                for(XmlAdapter adapter : adapters) {
                    unmarshaller.setAdapter(adapter);
                }
            }

            XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();
            Source src = new SAXSource(reader, new InputSource(new StringReader(xml)));
            JAXBElement<T> element = unmarshaller.unmarshal(src, className);

            result = element.getValue();

        }catch(JAXBException jaxbex) {
            logger.error("Error while unmarshalling xml", jaxbex);
            throw new IllegalStateException("Couldn't unmarshall xml string");
        } catch (SAXException e) {
            logger.error("Error while unmarshalling xml", e);
            throw new IllegalStateException("Couldn't unmarshall xml string");
        } catch (ParserConfigurationException e) {
            logger.error("Error while unmarshalling xml", e);
            throw new IllegalStateException("Couldn't unmarshall xml string");
        }

        return result;
    }

    public <T extends Object> T awareNSUnmarshall(String xml, Class<T> className) {
        // name space aware to false
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(false);
        // ToDo: need more implementation
        return null;
    }
}
