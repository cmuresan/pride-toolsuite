package uk.ac.ebi.pride.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.listener.MouseOverListener;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * IconLoader helps to load icon from file system
 * <p/>
 * User: rwang
 * Date: 22-Sep-2010
 * Time: 10:35:46
 */
public class GUIUtilities {
    private static final Logger logger = LoggerFactory.getLogger(GUIUtilities.class);
    /**
     * cache for icons has been loaded previously
     */
    private static Map<String, Icon> icons = Collections.synchronizedMap(new HashMap<String, Icon>());

    /**
     * Load icon
     *
     * @param iconName path the icon file.
     * @return Icon icon object.
     */
    public static Icon loadIcon(String iconName) {

        if (iconName == null) {
            return null;
        } else {
            iconName = iconName.trim();
        }

        Icon icon = icons.get(iconName);
        if (icon != null) {
            return icon;
        }
        
        URL iconURL = GUIUtilities.class.getClassLoader().getResource(iconName);
        icon = new ImageIcon(iconURL);
        icons.put(iconName, icon);

        return icon;
    }

    /**
     * Load an image icon, this method uses loadIcon.
     *
     * @param iconName  path to the icon file.
     * @return ImageIcon image icon object.
     */
    public static ImageIcon loadImageIcon(String iconName){
        Icon icon = loadIcon(iconName);
        return icon == null ? null : (ImageIcon)icon;
    }

    /**
     * Create a JLabel like button
     *
     * @param icon  icon for the button
     * @param title title for the button
     * @return JButton  button object
     */
    public static JButton createLabelLikeButton(Icon icon, String title) {
        JButton button = new JButton(title, icon);

        // set button look and feel to JLabel like
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setOpaque(false);

        // left align
        button.setHorizontalAlignment(JButton.LEFT);

        // mouse over listener
        button.addMouseListener(new MouseOverListener(button));

        return button;
    }

    /**
     * create a JLabel like button using a <code> action </code>
     *
     * @param action    action
     * @return JButton  button
     */
    public static JButton createLabelLikeButton(Action action) {
        JButton button = new JButton(action);

        // set button look and feel to JLabel like
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setOpaque(false);

        // left align
        button.setHorizontalAlignment(JButton.LEFT);

        // mouse over listener
        button.addMouseListener(new MouseOverListener(button));

        return button;
    }

    /**
     * Display a dialog box.
     *
     * @param comp    parent component
     * @param message message to show
     * @param title   dialog title
     */
    public static void message(Component comp, String message, String title) {
        JOptionPane.showMessageDialog(comp, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Display a error message dialog box
     *
     * @param comp    parent component
     * @param message error message to show
     * @param title   dialog title
     */
    public static void error(Component comp, String message, String title) {
        JOptionPane.showMessageDialog(comp, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Display a warning message dialog box
     *
     * @param comp  parent component
     * @param message   warning message to show
     * @param title warning dialog title
     */
    public static void warn(Component comp, String message, String title) {
        JOptionPane.showMessageDialog(comp, message, title, JOptionPane.WARNING_MESSAGE);
    }
}
