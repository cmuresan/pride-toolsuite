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

    public PrideXYDataSource(Double[] domainData, PrideData[] rangeData, PrideDataType type) {
        if (domainData.length != rangeData.length) {
            throw new IllegalArgumentException("Input data not correct!");
        }

        for (PrideData value : rangeData) {
            if (! value.getType().compatible(type)) {
                throw new IllegalArgumentException("There exists incompatible value " + value + " in range array!");
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

    public PrideXYDataSource filter(PrideDataType type) {
        List<Double> filterDomainData = new ArrayList<Double>();
        List<PrideData> filterRangeData = new ArrayList<PrideData>();

        PrideData data;
        for (int i = 0; i < rangeData.length; i++) {
            data = rangeData[i];
            if (data.getType().compatible(type)) {
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
