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
}


//~ Formatted by Jindent --- http://www.jindent.com
