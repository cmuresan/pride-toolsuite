package uk.ac.ebi.pride.chart.dataset;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public interface PridePlotDataSource<E> {
    public E getData();
    public void setData(E o);
}
