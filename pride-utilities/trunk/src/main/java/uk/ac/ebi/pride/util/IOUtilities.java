package uk.ac.ebi.pride.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;

/**
 * <code> IOUtilities </code> provides a list of convenient methods
 * to perform io operations.
 * <p/>
 * User: rwang
 * Date: 11-Oct-2010
 * Time: 11:06:10
 */
public class IOUtilities {
    private static final Logger logger = LoggerFactory.getLogger(IOUtilities.class);

    /**
     * This method return the installation path of the input <code> cs </code>
     *
     * @param cs class where the path is generated
     * @return URL  full path
     */
    public static URL getFullPath(Class cs) {
        return getFullPath(cs, null);
    }

    /**
     * This method return the full path of specified subPath
     *
     * @param cs      class where the path is generated
     * @param subPath sub path
     * @return URL  full path url
     */
    public static URL getFullPath(Class cs, String subPath) {
        if (cs == null) {
            throw new IllegalArgumentException("Input class cannot be NULL");
        }

        URL fullPath = null;

        CodeSource src = cs.getProtectionDomain().getCodeSource();
        if (src != null) {
            if (subPath == null) {
                fullPath = src.getLocation();
            } else {
                try {
                    fullPath = new URL(src.getLocation(), subPath);
                } catch (MalformedURLException e) {
                    logger.error("Failed to create a new URL based on: " + subPath);
                }
            }
        }

        return fullPath;
    }

    /**
     * Convert a url to a file object
     *
     * @param url input url
     * @return File file object represents the input url
     */
    public static File convertURLToFile(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("Input URL cannot be NULL");
        }

        // convert url to file
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            // this works for simple cases, like: file:/path/to/abc or file:///path/to/abc
            // however, this fails to work on paths which contains URL-unsafe characters, such as
            // file:///c:/Documents%20and%20Settings/
            file = new File(url.getPath());
        }

        return file;
    }
}
