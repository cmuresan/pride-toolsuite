package uk.ac.ebi.pride.chart.utils;

import java.util.List;

/**
 * <p>String Utilities</p>
 *
 * @author Antonio Fabregat
 * Date: 10-sep-2010
 * Time: 16:51:38
 */
public class StringUtils {

    public static String implode(String[] ary, String delim) {
        StringBuilder strBuilder = new StringBuilder("");
        for (int i = 0; i < ary.length; i++) {
            if (i != 0) strBuilder.append(delim);
            strBuilder.append(ary[i]);
        }
        return strBuilder.toString();
    }

    public static String implode(List<Integer> ary, String delim) {
        StringBuilder strBuilder = new StringBuilder("");
        for (int i = 0; i < ary.size(); i++) {
            if (i != 0) strBuilder.append(delim);
            strBuilder.append(ary.get(i).toString());
        }
        return strBuilder.toString();
    }
}
