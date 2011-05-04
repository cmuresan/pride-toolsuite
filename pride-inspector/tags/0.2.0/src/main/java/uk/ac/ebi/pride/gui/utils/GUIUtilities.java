package uk.ac.ebi.pride.gui.utils;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * GUIUtilities provides a list of methods to support GUI building process.
 *
 * User: rwang
 * Date: 17-Aug-2010
 * Time: 21:53:37
 */
public class GUIUtilities {
    /**
     * cache for icons has been loaded previously
     */
    private static Map<String, Icon> icons;

    /**
     * Load icon
     * @param iconName  path the icon file.
     * @return Icon icon object.
     */
    public static Icon loadIcon(String iconName) {
        if (iconName == null) {
            return null;
        }

        if (icons == null) {
            icons = new HashMap<String, Icon>();
        }

        Icon icon = icons.get(iconName);
        if (icon != null) {
            return icon;
        }

        URL url = ClassLoader.getSystemResource(iconName);
        icon = new ImageIcon(url);
        icons.put(iconName, icon);
        return icon;
    }
}
