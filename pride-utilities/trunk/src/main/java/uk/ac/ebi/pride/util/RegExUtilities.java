package uk.ac.ebi.pride.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code> RegExUtilities </code> provides convenient methods for handling regular expressions.
 *
 * User: rwang
 * Date: 10-Oct-2010
 * Time: 09:57:28
 */
public class RegExUtilities {

    /** regular expression pattern for email address */
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[A-Za-z0-9\\._%\\+\\-]+@[A-Za-z0-9\\.\\-]+\\.[A-Za-z]{2,4}");

    public static String getMatchedString(Pattern pattern, String str, int offset) {
        Matcher match = pattern.matcher(str);
        if (match.find()) {
            return match.group(offset).intern();
        } else {
            throw new IllegalStateException("Invalid ID in string: " + str);
        }
    }

    /**
     * Check whether <code> email </code> is a valid email address.
     *
     * @param email email string
     * @return boolean  true if it is email address.
     */
    public static boolean isValidEmail(String email) {
        Matcher m = EMAIL_ADDRESS_PATTERN.matcher(email);
        return m.matches();
    }
}
