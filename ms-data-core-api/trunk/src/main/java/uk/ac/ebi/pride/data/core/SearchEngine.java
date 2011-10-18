package uk.ac.ebi.pride.data.core;

//~--- non-JDK imports --------------------------------------------------------

import uk.ac.ebi.pride.engine.SearchEngineType;

import java.util.ArrayList;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

/**
 * SearchEngine store the original search engine title and identified search engine types.
 * <p/>
 * User: rwang, yperez
 * Date: Dec 2, 2010
 * Time: 10:12:31 AM
 */
public class SearchEngine extends Identifiable {

    /**
     * Identified search engine types using the data source
     */
    private List<SearchEngineType> searchEngineTypes = new ArrayList<SearchEngineType>();

    /**
     * @param searchengine
     */
    public SearchEngine(SearchEngine searchengine) {
        super(searchengine.getId(), searchengine.getName());
        this.searchEngineTypes = searchengine.getSearchEngineTypes();
    }

    /**
     * @param id
     * @param name
     */
    public SearchEngine(Comparable id, String name) {
        super(id, name);
        this.searchEngineTypes = null;
    }

    /**
     * @param id
     * @param name
     * @param searchEngineTypes
     */
    public SearchEngine(Comparable id, String name, List<SearchEngineType> searchEngineTypes) {
        super(id, name);

        if (searchEngineTypes != null) {
            this.searchEngineTypes.addAll(searchEngineTypes);
        }
    }

    /**
     * Get all the search engine types
     *
     * @return List<SearchEngineType>   a list of search engine types
     */
    public List<SearchEngineType> getSearchEngineTypes() {
        return searchEngineTypes;
    }

    /**
     * Set search engine types
     *
     * @param searchEngineTypes a list of search engine types
     */
    public void setSearchEngineTypes(List<SearchEngineType> searchEngineTypes) {
        this.searchEngineTypes.clear();
        this.searchEngineTypes.addAll(searchEngineTypes);
    }

    /**
     * Add search engine type
     *
     * @param searchEngineType search engine type
     */
    public void addSearchEngineType(SearchEngineType searchEngineType) {
        this.searchEngineTypes.add(searchEngineType);
    }
}



