package uk.ac.ebi.pride.gui.px;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entry represents each proteomexchange submission file
 *
 * @author Rui Wang
 * @version $Id$
 */
public class PxSubmissionEntry {

    public static final String ACCESSION = "px_accession";
    public static final String DOI = "doi";
    public static final String FILE_ID = "file_id";
    public static final String FILE_NAME = "file_name";
    public static final String FILE_TYPE = "file_type";
    public static final String PRIDE_ACC = "pride_accession";
    public static final String FILE_MAPPING = "file_mapping";
    public static final String FILE_SIZE = "file_size";
    private static final String COMMA = ",";

    private String accession;
    private String doi;
    private String fileID;
    private String fileName;
    private String fileType;
    private String prideAccession;
    private Set<String> fileMappings;
    private boolean isFileMapped;
    private double size;
    private boolean toDownload;

    private final Set<PxSubmissionEntry> parentPxSubmissionEntries;
    private final Set<PxSubmissionEntry> childPxSubmissionEntries;

    public PxSubmissionEntry(String accession, String doi) {
        this(accession, doi, null, null, null, null, null, 0.0);
    }

    public PxSubmissionEntry(String accession,
                             String doi,
                             String fileID,
                             String fileName,
                             String fileType,
                             String prideAccession,
                             String fileMapping,
                             double size) {
        this.accession = accession;
        this.doi = doi;
        this.fileID = fileID;
        this.fileName = fileName;
        this.fileType = fileType;
        this.prideAccession = prideAccession;
        this.fileMappings = createFileMappings(fileMapping);
        this.isFileMapped = false;
        this.size = size;
        this.toDownload = false;
        this.parentPxSubmissionEntries = new LinkedHashSet<PxSubmissionEntry>();
        this.childPxSubmissionEntries = new LinkedHashSet<PxSubmissionEntry>();
    }

    private Set<String> createFileMappings(String fileMapping) {
        Set<String> mappings = new LinkedHashSet<String>();
        if (fileMapping != null) {
            String[] parts = fileMapping.trim().split(COMMA);
            for (String part : parts) {
                String trimmedPart = part.trim();
                if (!"".equals(trimmedPart)){
                    mappings.add(trimmedPart);
                }
            }
        }
        return mappings;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPrideAccession() {
        return prideAccession;
    }

    public void setPrideAccession(String prideAccession) {
        this.prideAccession = prideAccession;
    }

    public boolean hasFileMappings() {
        return fileMappings.size() > 0;
    }

    public Set<String> getFileMappings() {
        return  fileMappings;
    }

    public boolean isFileMapped() {
        return isFileMapped;
    }

    public void setFileMapped(boolean fileMapped) {
        isFileMapped = fileMapped;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public boolean isToDownload() {
        return toDownload;
    }

    public void setToDownload(boolean toDownload) {
        this.toDownload = toDownload;
    }

    public int getNumberOfParent() {
        return parentPxSubmissionEntries.size();
    }

    public boolean hasParents() {
        return parentPxSubmissionEntries.size() > 0;
    }

    public boolean hasParent(PxSubmissionEntry parent) {
        return parentPxSubmissionEntries.contains(parent);
    }

    public Set<PxSubmissionEntry> getParents() {
        return parentPxSubmissionEntries;
    }

    public void addParent(PxSubmissionEntry parent) {
        this.parentPxSubmissionEntries.add(parent);
    }

    public void removeParent(PxSubmissionEntry parent) {
        this.parentPxSubmissionEntries.remove(parent);
    }

    public int getNumberOfChild() {
        return childPxSubmissionEntries.size();
    }

    public boolean hasChildren() {
        return childPxSubmissionEntries.size() > 0;
    }

    public boolean hasChild(PxSubmissionEntry child) {
        return childPxSubmissionEntries.contains(child);
    }

    public Set<PxSubmissionEntry> getChildren() {
        return childPxSubmissionEntries;
    }

    public void addChild(PxSubmissionEntry child) {
        this.childPxSubmissionEntries.add(child);
    }

    public void removeChild(PxSubmissionEntry child) {
        this.childPxSubmissionEntries.remove(child);
    }
}
