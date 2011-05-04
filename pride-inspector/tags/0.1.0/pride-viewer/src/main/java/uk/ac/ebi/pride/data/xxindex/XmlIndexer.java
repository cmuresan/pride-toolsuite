package uk.ac.ebi.pride.data.xxindex;

import psidev.psi.tools.xxindex.index.IndexElement;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Mar-2010
 * Time: 11:02:36
 */
public interface XmlIndexer {

    public List<String> getAllXmlStrings(String xpath);

    public int getCount(String xpath);

    public String getXmlString(String xpath, long offset);

    public List<String> getXmlStringWithinRange(String xpath, IndexElement range);

    public List<IndexElement> getIndexElements(String xpath);

    public Set<String> getXpath();
}
