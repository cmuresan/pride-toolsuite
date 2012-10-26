package uk.ac.ebi.pride.mzgraph.chart.data.annotation;

import org.jfree.data.xy.XYDataItem;

/**
 * Fragment ion annotation implementation.
 *
 * User: rwang
 * Date: 09-Jun-2010
 * Time: 19:27:17
 */
public class IonAnnotation extends XYDataItem implements PeakAnnotation {
    private IonAnnotationInfo info;

    public IonAnnotation(Number x, Number y, IonAnnotationInfo info) {
        super(x, y);
        this.info = info;
    }

    public IonAnnotation(double x, double y, IonAnnotationInfo info) {
        super(x, y);
        this.info = info;
    }

    @Override
    public Number getMz() {
        return this.getX();
    }

    @Override
    public Number getIntensity() {
        return this.getY();
    }

    @Override
    public IonAnnotationInfo getAnnotationInfo() {
        return info;
    }

    @Override
    public Object clone() {
        IonAnnotation newAnnotation = null;

        try {
            newAnnotation = (IonAnnotation) super.clone();
            IonAnnotationInfo newInfo = (IonAnnotationInfo) info.clone();
            newAnnotation.info = newInfo;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return newAnnotation;
    }
}
