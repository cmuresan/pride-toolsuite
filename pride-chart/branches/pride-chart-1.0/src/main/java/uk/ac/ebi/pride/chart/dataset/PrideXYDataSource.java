package uk.ac.ebi.pride.chart.dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public class PrideXYDataSource {
    private Double[] domainData;
    private PrideData[] rangeData;
    private PrideDataType type;
    private boolean includeSubType = false;

    public PrideXYDataSource(Double[] domainData, PrideData[] rangeData, PrideDataType type) {
        if (domainData.length != rangeData.length) {
            throw new IllegalArgumentException("Input data not correct!");
        }

        for (PrideData value : rangeData) {
            if (! value.getType().compatible(type)) {
                throw new IllegalArgumentException("There exists incompatible value " + value + " in range array!");
            }
            if (value.getType() != type) {
                includeSubType = true;
            }
        }

        this.type = type;
        this.domainData = domainData;
        this.rangeData = rangeData;
    }

    public Double[] getDomainData() {
        return domainData;
    }

    public PrideData[] getRangeData() {
        return rangeData;
    }

    public PrideDataType getType() {
        return type;
    }

    /**
     * Whether exists subType data in current data source.
     */
    public boolean isIncludeSubType() {
        return includeSubType;
    }

    public PrideXYDataSource filter(PrideDataType type) {
        if (! this.type.compatible(type)) {
            // current data source type not compatible with filter type. return empty.
            return null;
        }

        List<Double> filterDomainData = new ArrayList<Double>();
        List<PrideData> filterRangeData = new ArrayList<PrideData>();

        PrideData data;
        for (int i = 0; i < rangeData.length; i++) {
            data = rangeData[i];
            if (data.getType().equals(type)) {
                filterDomainData.add(domainData[i]);
                filterRangeData.add(rangeData[i]);
            }
        }

        int size = filterDomainData.size();
        return new PrideXYDataSource(
                filterDomainData.toArray(new Double[size]),
                filterRangeData.toArray(new PrideData[size]),
                type
        );
    }
}
