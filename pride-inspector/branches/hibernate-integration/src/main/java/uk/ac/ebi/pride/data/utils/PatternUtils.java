package uk.ac.ebi.pride.data.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 24-Mar-2010
 * Time: 12:18:24
 */
public class PatternUtils {
    public static String getMatchedString(Pattern pattern, String str, int offset) {
        Matcher match = pattern.matcher(str);
        if (match.find()) {
            return match.group(offset).intern();
        } else {
            throw new IllegalStateException("Invalid ID in string: " + str);
        }
    }
}
