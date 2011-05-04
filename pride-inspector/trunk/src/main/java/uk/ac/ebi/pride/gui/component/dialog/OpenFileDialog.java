package uk.ac.ebi.pride.gui.component.dialog;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * User: rwang
 * Date: 11-Feb-2010
 * Time: 11:54:20
 */
public class OpenFileDialog extends JFileChooser {
    private String[] fileFormats;

    /**
     * 
     * @param path  default path
     * @param title title for the component
     * @param extensions a list of supported file extensions
     */
    public OpenFileDialog(String path, String title, String ... extensions) {
        super(path);
        initialize(title, extensions);
    }

    private void initialize(String title, String[] extensions) {
        this.fileFormats = extensions;
        this.setDialogTitle(title);
        this.setDialogType(JFileChooser.OPEN_DIALOG);
        this.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.setMultiSelectionEnabled(true);
        this.setFileFilter(new InnerFileFilter());
    }

    private class InnerFileFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            boolean result = false;
            String fileName = f.getName().toLowerCase();

            for (String fileFormat : fileFormats) {
                if (fileName.endsWith(fileFormat))
                    result = true;
            }
            
            result = f.isDirectory() || result;
            
            return result;
        }

        @Override
        public String getDescription() {
            StringBuilder str = new StringBuilder();
            for(String fileFormat : fileFormats) {
                if (str.length() != 0)
                    str.append(" or ");

                str.append(fileFormat);
            }
            return str.toString();
        }
    }
}
