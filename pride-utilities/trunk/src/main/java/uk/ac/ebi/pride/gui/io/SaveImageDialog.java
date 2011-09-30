package uk.ac.ebi.pride.gui.io;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Dialog to save to an image file format
 * <p/>
 * User: rwang
 * Date: 07/09/2011
 * Time: 10:55
 */
public class SaveImageDialog extends JFileChooser {

    /**
     * Constructor
     *
     * @param currentDirectory initial directory
     * @param defaultFileName  default file name for saving
     */
    public SaveImageDialog(File currentDirectory, String defaultFileName) {
        super(currentDirectory);
        // set dialog title
        this.setDialogTitle("Save File");
        // set dialog default file name
        if (defaultFileName != null) {
            this.setSelectedFile(new File(defaultFileName));
        }
        // set dialog type
        this.setDialogType(JFileChooser.SAVE_DIALOG);
        // set selection mode
        this.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // disable multiple selection
        this.setMultiSelectionEnabled(false);
        // set file filter
        this.addChoosableFileFilter(new ImageFileFilter(FileExtension.JPEG.getExtension(), FileExtension.JPEG.getExtensionDescription()));
        this.addChoosableFileFilter(new ImageFileFilter(FileExtension.PNG.getExtension(), FileExtension.PNG.getExtensionDescription()));
        this.addChoosableFileFilter(new ImageFileFilter(FileExtension.GIF.getExtension(), FileExtension.GIF.getExtensionDescription()));
        this.addChoosableFileFilter(new ImageFileFilter(FileExtension.SVG.getExtension(), FileExtension.SVG.getExtensionDescription()));
        this.addChoosableFileFilter(new ImageFileFilter(FileExtension.PDF.getExtension(), FileExtension.PDF.getExtensionDescription()));
    }

    /**
     * JPEG file filter
     */
    private class ImageFileFilter extends FileFilter {
        private String extension;
        private String description;

        private ImageFileFilter(String extension, String description) {
            this.extension = extension;
            this.description = description;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return false;
            }
            String fileName = f.getName().toLowerCase();
            return fileName.endsWith(extension.toLowerCase());
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
