package uk.ac.ebi.pride.chart.utils;

import java.util.HashMap;

/**
 * <p> Container of the known amino-acids.</p>
 *
 * @author Antonio Fabregat
 * Date: 03-ago-2010
 * Time: 16:05:06
 */
public class KnownAminoAcids {

    private static HashMap<Double, String> mapDouble = new HashMap<Double, String>();
    private static HashMap<Integer, String> mapInteger = new HashMap<Integer, String>();

    static {
        mapDouble.put(57.052, "G");
        mapDouble.put(71.0788, "A");
        mapDouble.put(71.0788, "A");
        mapDouble.put(87.0782, "S");
        mapDouble.put(97.1167, "P");
        mapDouble.put(99.1326, "V");
        mapDouble.put(101.1051, "T");
        mapDouble.put(103.1448, "C");
        mapDouble.put(113.1595, "I");
        mapDouble.put(113.1595, "L");
        mapDouble.put(114.1039, "N");
        mapDouble.put(115.0886, "D");
        mapDouble.put(128.1308, "Q");
        mapDouble.put(128.1742, "K");
        mapDouble.put(129.1155, "E");
        mapDouble.put(131.1986, "M");
        mapDouble.put(137.1412, "H");
        mapDouble.put(147.1766, "F");
        mapDouble.put(156.1876, "R");
        mapDouble.put(163.1760, "Y");
        mapDouble.put(186.2133, "W");

        for (double value : mapDouble.keySet()) {
            int v = (int) Math.round(value);
            mapInteger.put(v, mapDouble.get(value));
        }
    }

    /**
     * Returns the letter representing the amino-acid or null if the passed value does not exist in the list
     *
     * @param value the m/z delta value
     * @return the letter representing the amino-acid or null if the passed value does not exist in the list
     */
    public static String getName(int value) {
        return mapInteger.get(value);
    }

    /**
     * Returns the letter representing the amino-acid or null if the passed value does not exist in the list
     * @param value the m/z delta value
     * @return the letter representing the amino-acid or null if the passed value does not exist in the list
     */
    public static String getName(double value) {
        return mapDouble.get(value);
    }
}