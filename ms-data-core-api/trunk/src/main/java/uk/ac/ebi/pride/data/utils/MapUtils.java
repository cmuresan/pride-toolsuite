package uk.ac.ebi.pride.data.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rui Wang
 * @version $Id$
 */
public final class MapUtils {

    public static <T, V> Map<T, V> createMapFromMap(Map<T, V> map) {
        Map<T, V> newMap = new HashMap<T, V>();

        if (map != null) {
            newMap.putAll(map);
        }

        return newMap;
    }

    public static <T, V> void replaceValuesInMap(Map<T, V> from, Map<T, V> to) {
        to.clear();
        if (from != null) {
            to.putAll(from);
        }
    }
}
