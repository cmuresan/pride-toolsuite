package uk.ac.ebi.pride.data.core;

/**
 * InstrumentComponent is key to keep the order of different instrument
 * component.
 * <p/>
 * User: rwang
 * Date: 18-May-2010
 * Time: 14:40:24
 */
public class InstrumentComponent extends ParamGroup {

    /**
     * order of the component among all instruments
     */
    private int order = -1;

    /**
     * constructor
     *
     * @param order  required
     * @param params optional
     */
    public InstrumentComponent(int order, ParamGroup params) {
        super(params);
        setOrder(order);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        InstrumentComponent that = (InstrumentComponent) o;

        if (order != that.order) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + order;
        return result;
    }
}



