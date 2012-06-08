package uk.ac.ebi.pride.gui.px;

/**
 * Entry represents each proteomexchange submission file
 *
 * @author Rui Wang
 * @version $Id$
 */
public class PxSubmissionEntry {

    public static final String ACCESSION = "accession";
    public static final String DOI = "doi";
    public static final String FILE_ID = "file_id";
    public static final String FILE_NAME = "file_name";
    public static final String FILE_TYPE = "file_type";
    public static final String PRIDE_ACC = "pride_accession";
    public static final String FILE_MAPPING = "file_mapping";
    public static final String FILE_SIZE = "file_size";

    private String accession;
    private String doi;
    private String fileID;
    private String fileName;
    private String fileType;
    private String prideAccession;
    private String fileMapping;
    private double size;
    private boolean toDownload;

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
        this.fileMapping = fileMapping;
        this.size = size;
        this.toDownload = false;
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

    public String getFileMapping() {
        return fileMapping;
    }

    public void setFileMapping(String fileMapping) {
        this.fileMapping = fileMapping;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PxSubmissionEntry)) return false;

        PxSubmissionEntry that = (PxSubmissionEntry) o;

        if (accession != null ? !accession.equals(that.accession) : that.accession != null) return false;
        if (doi != null ? !doi.equals(that.doi) : that.doi != null) return false;
        if (fileID != null ? !fileID.equals(that.fileID) : that.fileID != null) return false;
        if (fileMapping != null ? !fileMapping.equals(that.fileMapping) : that.fileMapping != null) return false;
        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;
        if (fileType != null ? !fileType.equals(that.fileType) : that.fileType != null) return false;
        if (prideAccession != null ? !prideAccession.equals(that.prideAccession) : that.prideAccession != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = accession != null ? accession.hashCode() : 0;
        result = 31 * result + (doi != null ? doi.hashCode() : 0);
        result = 31 * result + (fileID != null ? fileID.hashCode() : 0);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (fileType != null ? fileType.hashCode() : 0);
        result = 31 * result + (prideAccession != null ? prideAccession.hashCode() : 0);
        result = 31 * result + (fileMapping != null ? fileMapping.hashCode() : 0);
        return result;
    }
}
