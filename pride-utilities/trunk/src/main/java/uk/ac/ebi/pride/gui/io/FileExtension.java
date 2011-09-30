package uk.ac.ebi.pride.gui.io;

/**
 * Various file extensions and descriptions
 *
 * User: rwang
 * Date: 07/09/2011
 * Time: 11:23
 */
public enum FileExtension {
    JPEG (".jpg", "*.jpg, *.JPG"),
    PDF (".pdf", "*.pdf, *.PDF"),
    SVG (".svg", "*.svg, *.SVG"),
    PNG (".png", "*.png, *.PNG"),
    GIF (".gif", "*.gif, *.GIF");


    private String extension;
    private String extensionDescription;

    private FileExtension(String extension, String extensionDescription) {
        this.extension = extension;
        this.extensionDescription = extensionDescription;
    }

    public String getExtension() {
        return extension;
    }

    public String getExtensionDescription() {
        return extensionDescription;
    }
}
