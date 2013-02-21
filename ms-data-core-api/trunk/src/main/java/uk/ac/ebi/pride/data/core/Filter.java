package uk.ac.ebi.pride.data.core;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 05/08/11
 * Time: 17:11
 */
public class Filter {
    private ParamGroup exclude    = null;
    private ParamGroup filterType = null;
    private ParamGroup include    = null;

    public Filter(ParamGroup filterType, ParamGroup include, ParamGroup exclude) {
        this.filterType = filterType;
        this.include    = include;
        this.exclude    = exclude;
    }

    public ParamGroup getFilterType() {
        return filterType;
    }

    public void setFilterType(ParamGroup filterType) {
        this.filterType = filterType;
    }

    public ParamGroup getInclude() {
        return include;
    }

    public void setInclude(ParamGroup include) {
        this.include = include;
    }

    public ParamGroup getExclude() {
        return exclude;
    }

    public void setExclude(ParamGroup exclude) {
        this.exclude = exclude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Filter filter = (Filter) o;

        if (exclude != null ? !exclude.equals(filter.exclude) : filter.exclude != null) return false;
        if (filterType != null ? !filterType.equals(filter.filterType) : filter.filterType != null) return false;
        if (include != null ? !include.equals(filter.include) : filter.include != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = exclude != null ? exclude.hashCode() : 0;
        result = 31 * result + (filterType != null ? filterType.hashCode() : 0);
        result = 31 * result + (include != null ? include.hashCode() : 0);
        return result;
    }
}



