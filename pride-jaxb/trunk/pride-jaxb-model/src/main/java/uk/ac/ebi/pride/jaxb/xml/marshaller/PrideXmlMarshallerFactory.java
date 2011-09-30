package uk.ac.ebi.pride.jaxb.xml.marshaller;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jaxb.model.ExperimentCollection;
import uk.ac.ebi.pride.jaxb.model.ModelConstants;
import uk.ac.ebi.pride.jaxb.model.PrideXmlObject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13-Aug-2010
 * Time: 14:15:35
 * To change this template use File | Settings | File Templates.
 */
public class PrideXmlMarshallerFactory {

    private static final Logger logger = Logger.getLogger(PrideXmlMarshallerFactory.class);

    private static PrideXmlMarshallerFactory instance = new PrideXmlMarshallerFactory();
    private static JAXBContext jc = null;

    private PrideXmlMarshallerFactory() {
    }

    public static PrideXmlMarshallerFactory getInstance() {
        return instance;
    }

    public PrideXmlMarshaller initializeMarshaller() {

        try {
            // Lazy caching of the JAXB Context.
            if (jc == null) {
                jc = JAXBContext.newInstance(ModelConstants.MODEL_PKG);
            }

            //create unmarshaller
            PrideXmlMarshaller pm = new PrideMarshallerImpl();
            logger.debug("Marshaller Initialized");

            return pm;

        } catch (JAXBException e) {
            logger.error("PrideXmlMarshaller.initializeMarshaller", e);
            throw new IllegalStateException("Could not initialize marshaller", e);
        }
    }

    private class PrideMarshallerImpl implements PrideXmlMarshaller {

        private Marshaller marshaller = null;

        private PrideMarshallerImpl() throws JAXBException {
            marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        }

        public <T extends PrideXmlObject> String marshall(T object) {
            StringWriter sw = new StringWriter();
            this.marshall(object, sw);
            return sw.toString();
        }

        public <T extends PrideXmlObject> void marshall(T object, OutputStream os) {
            this.marshall(object, new OutputStreamWriter(os));
        }

        public <T extends PrideXmlObject> void marshall(T object, Writer out) {

            if (object == null) {
                throw new IllegalArgumentException("Cannot marshall a NULL object");
            }

            try {

                // Set JAXB_FRAGMENT_PROPERTY to true for all objects that do not have
                // a @XmlRootElement annotation
                if (!(object instanceof ExperimentCollection)) {
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                } else {
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
                }

                QName aQName = ModelConstants.getQNameForClass(object.getClass());
                marshaller.marshal(new JAXBElement(aQName, object.getClass(), object), out);

            } catch (JAXBException e) {
                logger.error("PrideXmlMarshaller.marshall", e);
                throw new IllegalStateException("Error while marshalling object:" + object.toString());
            }

        }

    }

}
