package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.engine.SearchEngineType;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchEngine store the original search engine title and identified search engine types.
 *
 * User: rwang
 * Date: Dec 2, 2010
 * Time: 10:12:31 AM
 */
public class SearchEngine {
    /**
     * original search engine title from data source
     */
    private String originalTitle;
    /**
     * Identified search engine types using the data source
     */
    private List<SearchEngineType> searchEngineTypes = new ArrayList<SearchEngineType>();

    public SearchEngine(String originalTitle) {
        this(originalTitle, null);
    }

    public SearchEngine(String originalTitle, List<SearchEngineType> searchEngineTypes) {
        this.originalTitle = originalTitle;
        if (searchEngineTypes != null) {
            this.searchEngineTypes.addAll(searchEngineTypes);
        }
    }

    /**
     * Get original title from data source
     *
     * @return  String  original title
     */
    public String getOriginalTitle() {
        return originalTitle;
    }

    /**
     * Set original title from data source
     *
     * @param originalTitle original title
     */
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
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
     * @param searchEngineTypes a list of search engine types
     */
    public void setSearchEngineTypes(List<SearchEngineType> searchEngineTypes) {
        this.searchEngineTypes.clear();
        this.searchEngineTypes.addAll(searchEngineTypes);
    }

    /**
     * Add search engine type
     *
     * @param searchEngineType  search engine type
     */
    public void addSearchEngineType(SearchEngineType searchEngineType) {
        this.searchEngineTypes.add(searchEngineType);
    }
}
