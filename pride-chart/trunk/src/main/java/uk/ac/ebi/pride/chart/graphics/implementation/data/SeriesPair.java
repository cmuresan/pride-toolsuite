package uk.ac.ebi.pride.chart.graphics.implementation.data;

/**
 * <p>A generic wrap for every point of a series pair</p>
 *
 * @author Antonio Fabregat
 * Date: 01-nov-2010
 * Time: 9:43:03
 */
public class SeriesPair<T,U> {
    /**
     * Contains the x value of the pair
     */
    private T x;

    /**
     * Contains the y value of the pair
     */
    private U y;

    /**
     * <p> Creates an instance of this SeriesPair object, setting all fields as per description below.</p>
     *
     * @param x the x value of the pair
     * @param y the y value of the pair
     */
    public SeriesPair(T x, U y) {
        this.x = x;
        this.y = y;
    }
   
    /**
     * Returns the x value of the pair
     *
     * @return the x value of the pair
     */
    public T getX() {
        return x;
    }

    /**
     * Returns the y value of the pair
     *
     * @return the y value of the pair
     */
    public U getY() {
        return y;
    }
}
