package uk.ac.ebi.pride.data.core;

/**
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 05/08/11
 * Time: 17:11
 * To change this template use File | Settings | File Templates.
 */
public class Filter {

    private ParamGroup filterType = null;

    private ParamGroup include = null;

    private ParamGroup exclude = null;

    public Filter(ParamGroup filterType, ParamGroup include, ParamGroup exclude) {
        this.filterType = filterType;
        this.include = include;
        this.exclude = exclude;
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
}
