package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.engine.SearchEngineType;

import java.util.ArrayList;
import java.util.List;

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
    private List<SearchEngineType> searchEngineTypes;

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
        this.searchEngineTypes = new ArrayList<SearchEngineType>();
    }

    /**
     * @param id
     * @param name
     * @param searchEngineTypes
     */
    public SearchEngine(Comparable id, String name, List<SearchEngineType> searchEngineTypes) {
        super(id, name);

        if (searchEngineTypes != null) {
            this.searchEngineTypes = new ArrayList<SearchEngineType>();
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
        CollectionUtils.replaceValuesInCollection(searchEngineTypes, this.searchEngineTypes);
    }

    /**
     * Add search engine type
     *
     * @param searchEngineType search engine type
     */
    public void addSearchEngineType(SearchEngineType searchEngineType) {
        this.searchEngineTypes.add(searchEngineType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchEngine)) return false;
        if (!super.equals(o)) return false;

        SearchEngine that = (SearchEngine) o;

        if (!searchEngineTypes.equals(that.searchEngineTypes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + searchEngineTypes.hashCode();
        return result;
    }
}



