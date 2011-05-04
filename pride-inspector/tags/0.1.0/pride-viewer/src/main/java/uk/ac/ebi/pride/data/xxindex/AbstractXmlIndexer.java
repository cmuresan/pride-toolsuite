package uk.ac.ebi.pride.data.xxindex;

import org.apache.log4j.Logger;
import psidev.psi.tools.xxindex.StandardXmlElementExtractor;
import psidev.psi.tools.xxindex.StandardXpathAccess;
import psidev.psi.tools.xxindex.XmlElementExtractor;
import psidev.psi.tools.xxindex.XpathAccess;
import psidev.psi.tools.xxindex.index.IndexElement;
import psidev.psi.tools.xxindex.index.XpathIndex;
import uk.ac.ebi.pride.data.utils.PatternUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Mar-2010
 * Time: 12:12:53
 */
public abstract class AbstractXmlIndexer implements XmlIndexer{
    private static final Logger logger = Logger.getLogger(AbstractXmlIndexer.class.getName());

    private File xmlFile = null;
    private XpathAccess xpathAccess = null;
    private XmlElementExtractor xmlExtractor = null;
    private XpathIndex index = null;

    public AbstractXmlIndexer(File xml, Collection<String> xpathTobeIndexed) {
        if (xml == null) {
            throw new IllegalArgumentException("Xml file to be indexed must not be null");
        } else if (!xml.exists()) {
            throw new IllegalArgumentException("Xml file to be indexed does not exist: " + xml.getAbsolutePath());
        }
        
        this.xmlFile = xml;
        try {
            // create xxindex
            xpathAccess = new StandardXpathAccess(xmlFile, new HashSet<String>(xpathTobeIndexed));

            // create xml extractor
            xmlExtractor = new StandardXmlElementExtractor();
            xmlExtractor.setEncoding(xmlExtractor.detectFileEncoding(xmlFile.toURI().toURL()));

            // create index
            index = xpathAccess.getIndex();
        }catch(IOException ioe) {
            logger.error("AbstractXmlIndexer constructor, error while generating index", ioe);
            throw new IllegalStateException("Fail to generate index for file: " + xmlFile);
        }
    }

    protected String readXml(IndexElement byteRange) throws IOException{
        if (byteRange == null) {
            throw new IllegalArgumentException("Cannot read NULL byte range");
        }

        return xmlExtractor.readString(byteRange.getStart(), byteRange.getStop(), xmlFile);
    }

    protected Map<IndexElement, String> initMapCache(String xpath, Pattern pattern, int offset) throws IOException {
        Map<IndexElement, String> idMap = new LinkedHashMap<IndexElement, String>();
        List<IndexElement> ranges = this.getIndexElements(xpath);

        // get xml strings
        for (IndexElement byteRange : ranges) {
            String xml = readXml(byteRange);
            String id = PatternUtils.getMatchedString(pattern, xml, offset);
            if (id != null) {
                idMap.put(byteRange, id);
            }
        }
        return idMap;
    }

    @Override
    public List<String> getAllXmlStrings(String xpath) {
        List<IndexElement> elements = this.getIndexElements(xpath);
        List<String> xmlList = null;
        if (elements != null && !elements.isEmpty()) {
            xmlList = new ArrayList<String>();
            try {
                for(IndexElement element : elements) {
                    xmlList.add(readXml(element));
                }
            } catch(IOException ioe) {
                logger.error("AbstractxmlIndexer.getAllXmlStrings(String, long)", ioe);
                throw new IllegalStateException("Could not extract XML from file: " + xmlFile);
            }
        }
        return xmlList;
    }

    @Override
    public int getCount(String xpath) {
        int retval = 0;
        List<IndexElement> tmpList = this.getIndexElements(xpath);
        if (tmpList != null){
            retval = tmpList.size();
        }
        return retval;
    }

    @Override
    public String getXmlString(String xpath, long offset) {
        String retVal = null;
        List<IndexElement> indexElements = this.getIndexElements(xpath);

        for (IndexElement indexElement : indexElements) {
            if (indexElement.getStart() == offset) {
                // found what we are looking for
                try {
                    retVal = readXml(indexElement);
                } catch (IOException ioe) {
                    logger.error("AbstractxmlIndexer.getXmlString(String, long)", ioe);
                    throw new IllegalStateException("Could not extract XML from file: " + xmlFile);
                }
                break; 
            }
        }
        return retVal;
    }

    @Override
    public List<String> getXmlStringWithinRange(String xpath, IndexElement refRange) {
        List<String> xmlList = null;

        List<IndexElement> ranges = this.getIndexElements(xpath);

        if (ranges != null && !ranges.isEmpty()) {
            xmlList = new ArrayList<String>();
            long start = refRange.getStart();
            long end = refRange.getStop();
            for (IndexElement range : ranges) {
                if (range.getStart() >= start && range.getStop()<=end) {
                    try {
                        xmlList.add(readXml(range));
                    } catch (IOException ioe) {
                        logger.error("AbstractxmlIndexer.getXmlStringWithinRange(String, IndexElement)", ioe);
                        throw new IllegalStateException("Could not extract XML from file: " + xmlFile);
                    }
                }
            }
        }

        return xmlList;
    }

    @Override
    public List<IndexElement> getIndexElements(String xpath) {
        List<IndexElement> elements = index.getElements(xpath);
        if (elements != null && elements.size()>1) {
            elements = IndexElementSorter.sortList(elements);            
        }

        return elements;
    }

    @Override
    public Set<String> getXpath() {
        return index.getKeys();
    }
}
