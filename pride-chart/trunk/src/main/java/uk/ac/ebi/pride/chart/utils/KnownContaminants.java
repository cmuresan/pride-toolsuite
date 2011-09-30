package uk.ac.ebi.pride.chart.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p> Container of the known contaminants.</p>
 *
 * @author Antonio Fabregat
 *         Date: 04-ago-2010
 *         Time: 16:39:27
 */
public class KnownContaminants {
    private static HashMap<Double, String[]> mapDouble = new HashMap<Double, String[]>();
    private static HashMap<Integer, String[]> mapInteger = new HashMap<Integer, String[]>();

    static {
        Integer i = 1;
        mapDouble.put(14.01565, new String[]{i.toString(), "CH2"});
        mapDouble.put(15.99492, new String[]{(++i).toString(), "O"});
        mapDouble.put(18.01057, new String[]{"H2O", "H2O"});
        mapDouble.put(28.03130, new String[]{(++i).toString(), "C2H4"});
        mapDouble.put(32.02622, new String[]{(++i).toString(), "CH3OH"});
        mapDouble.put(41.02655, new String[]{(++i).toString(), "CH3CN"});
        mapDouble.put(42.04695, new String[]{(++i).toString(), "C3H6"});
        mapDouble.put(44.0, new String[]{"peg", "peg"});
        //mapDouble.put(44.02622, new String[]{(i++).toString(), "C2H4O"});
        mapDouble.put(49.99681, new String[]{(++i).toString(), "CF2"});
        mapDouble.put(53.00323, new String[]{(++i).toString(), "NH4Cl"});
        mapDouble.put(56.06260, new String[]{(++i).toString(), "C4H8"});
        mapDouble.put(57.95862, new String[]{(++i).toString(), "NaCl - C3H6O"});
        //mapDouble.put(58.04187, new String[]{(i++).toString(), "C3H6O"});
        mapDouble.put(63.03203, new String[]{(++i).toString(), "CHOONH4"});
        mapDouble.put(67.98742, new String[]{(++i).toString(), "NaHCO2 - CHOONa"});
        //mapDouble.put(67.98742, new String[]{(i++).toString(), "CHOONa"});
        mapDouble.put(72.03953, new String[]{(++i).toString(), "OH"});
        mapDouble.put(73.93256, new String[]{(++i).toString(), "KCl - O-Si(CH3)2)"});
        //mapDouble.put(74.01879, new String[]{(i++).toString(), "O-Si(CH3)2)"});
        mapDouble.put(78.01394, new String[]{(++i).toString(), "C2H6OS"});
        mapDouble.put(82.00307, new String[]{(++i).toString(), "NaCH3CO2"});
        mapDouble.put(84.05159, new String[]{(++i).toString(), "C2D6OS"});
        mapDouble.put(106.90509, new String[]{(++i).toString(), "107Ag"});
        mapDouble.put(108.90476, new String[]{(++i).toString(), "109Ag"});
        mapDouble.put(113.99286, new String[]{(++i).toString(), "CF3COOH"});
        mapDouble.put(121.93828, new String[]{(++i).toString(), "NaClO4"});
        mapDouble.put(135.97481, new String[]{(++i).toString(), "NaCF3CO2"});
        mapDouble.put(162.05283, new String[]{(++i).toString(), "C6H10O5"});
        mapDouble.put(226.16813, new String[]{(++i).toString(), "C12H22N2O2"});
        mapDouble.put(259.80992, new String[]{(++i).toString(), "CsI"});
        mapDouble.put(288.13713, new String[]{"SDS", "SDS"});

        for (double value : mapDouble.keySet()) {
            int v = (int) Math.round(value);
            mapInteger.put(v, mapDouble.get(value));
        }
    }

    /**
     * Returns a string representing the contaminant or null if the passed value does not exist in the list
     *
     * @param value the m/z delta value
     * @return the string representing the contaminant or null if the passed value does not exist in the list
     */
    public static String[] getName(int value) {
        return mapInteger.get(value);
    }

    /**
     * Returns the letter representing the contaminant or null if the passed value does not exist in the list
     *
     * @param value the m/z delta value
     * @return the letter representing the contaminant or null if the passed value does not exist in the list
     */
    public static String[] getName(double value) {
        return mapDouble.get(value);
    }

    /**
     * Returns a hashmap with the values to be included in a legend of the contaminants numbers
     *
     * @return a legend of the contaminants numbers
     */
    public static Map<String, String> getLegend(){
        Map<String, String> legend = new HashMap<String, String>();
        for(int value: mapInteger.keySet()){
            String[] legendValues = mapInteger.get(value);
            legend.put(legendValues[0], legendValues[1]);
        }
        return legend;
    }
}

