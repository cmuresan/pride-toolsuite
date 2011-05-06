package uk.ac.ebi.pride.gui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.url.HttpUtilities;
import uk.ac.ebi.pride.util.InternetChecker;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Check whether there is a new update
 * <p/>
 * User: rwang
 * Date: 11-Nov-2010
 * Time: 17:19:36
 */
public class UpdateChecker {

    public static final Logger logger = LoggerFactory.getLogger(UpdateChecker.class);

    /**
     * Check whether there is a new update
     *
     * @return boolean return true if there is a new update.
     */
    public static boolean hasUpdate() {
        boolean toUpdate = false;

        if (InternetChecker.check()) {
            // get the url for checking the update
            DesktopContext context = Desktop.getInstance().getDesktopContext();
            String website = context.getProperty("pride.inspector.website");
            String name = context.getProperty("pride.inspector.name");
            String version = context.getProperty("pride.inspector.version");

            try {
                URL url = new URL(website + "/downloads/detail?name=" + name + "-" + version + ".zip");
                // connect to the url
                int response = ((HttpURLConnection) url.openConnection()).getResponseCode();
                if (response == 404) {
                    toUpdate = true;
                } else {
                    // parse the web page
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.toLowerCase().contains("label:deprecated")) {
                            toUpdate = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to check for updates", e);
            }
        }

        return toUpdate;
    }

    /**
     * Show update dialog
     */
    public static void showUpdateDialog() {
        int option = JOptionPane.showConfirmDialog(null, "<html><b>A new version of PRIDE Inspector is available</b>.<br><br> " +
                "Press <b>Yes</b> to open a web page where you can download PRIDE Inspector manually <br> and learn more about the new version.</html>", "Update Info", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            DesktopContext context = Desktop.getInstance().getDesktopContext();
            String website = context.getProperty("pride.inspector.website");
            HttpUtilities.openURL(website);
        }
    }
}