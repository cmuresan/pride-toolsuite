package uk.ac.ebi.pride.util;

/**
 * General functions for all libraries in PRIDE Related with String handling
 */
public class StringUtils {
    /**
     * Check if an string is empty. Return tru if the string is null or the length is 0.
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}
