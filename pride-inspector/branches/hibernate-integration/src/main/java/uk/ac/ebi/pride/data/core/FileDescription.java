package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Information pertaining to the entire dataset.
 *
 * User: rwang
 * Date: 25-Apr-2010
 * Time: 15:40:47
 */
public class FileDescription implements MassSpecObject {
    /**
     * This summaries the different type of spectra can be expected in this dataset
     *
     * In mzML 1.1.0.1, the following cv terms must be added:
     *
     * 1. Must include one or more child terms of "data file content"
     * (total ion current chromatogram, precursor ion spectrum and et al)
     *
     * 2. May include only one child term of "spectrum representation" (centroid spectrum,
     * profile spectrum)
     */
    private ParamGroup fileContent = null;
    /** List and descriptions of the source files */
    private List<SourceFile> sourceFiles = null;
    /** 
     * List of contacts
     *
     * In mzMl 1.1.0.1, each contact must have the following cv terms:
     *
     * 1. May include one or more child terms of "contact person attribute"
     * (contact name, contact address, contact email and et al)
     */
    private List<ParamGroup> contacts = null;

    /**
     * Constructor
     * @param fileContent   required.
     * @param sourceFiles   optional.
     * @param contacts  optional.
     */
    public FileDescription(ParamGroup fileContent,
                           List<SourceFile> sourceFiles,
                           List<ParamGroup> contacts) {
        setFileContent(fileContent);
        setSourceFiles(sourceFiles);
        setContacts(contacts);
    }

    public ParamGroup getFileContent() {
        return fileContent;
    }

    public void setFileContent(ParamGroup fileContent) {
        if (fileContent == null) {
            throw new IllegalArgumentException("File description's file content can not be null");
        } else {
            this.fileContent = fileContent;
        }
    }

    public List<SourceFile> getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(List<SourceFile> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public List<ParamGroup> getContacts() {
        return contacts;
    }

    public void setContacts(List<ParamGroup> contacts) {
        this.contacts = contacts;
    }
}
