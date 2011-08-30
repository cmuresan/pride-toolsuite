package uk.ac.ebi.pride.data.coreIdent;

import java.util.List;

/**
 * Description of source file, including identification file, location and type.
 * the attributes fileFormat is used to manage the cvterm for file format in MzIdentMl
 * and the externalFormatDocumentURI is used to store the information of the external shcema.
 * <p/>
 * In mzML 1.1.0.1, the following cv terms must be added:
 * <p/>
 * 1. Must include only one a child term of "native spectrum identifier format"
 * (thermo nativeID format, waters nativeID format, WIFF nativeID format and et al)
 * <p/>
 * 2. Must include one or more child terms of "data file check-sum type" (MD5, SHA-1)
 * <p/>
 * 3. Must include only one child term of "source file type" (waters raw file,
 * ABI WIFF file, Thermo RAW file and et al)
 * <p/>
 * User: rwang, yperez
 * Date: 04-Feb-2010
 * Time: 15:53:36
 */
public class SourceFile extends IdentifiableParamGroup {
    /**
     * location of the source file
     */
    private String path = null;
    /**
     * The format of the ExternalData file, for example "tiff" for image files.
     */
    private CvParam fileFormat = null;
    /**
     * A URI to access documentation and tools to interpret the external format
     * of the ExternalData instance. For example, XML Schema or static libraries
     * (APIs) to access binary formats.
     */
    private String externalFormatDocumentationURI = null;

    /**
     * Constructor to the MzMl File Source
     * @param id
     * @param name
     * @param path
     */
    public SourceFile(String id, String name, String path) {
        super(id, name);
        this.path = path;
    }

    /**
     * Constructor for special cases were you don't have and Id
     * @param name
     * @param path
     */
    public SourceFile(String name, String path) {
        //there should be a single source file per spectrum
        super("", name);
        this.path = path;
    }

    /**
     * Constructor to the MzMl File Source
     * @param params
     * @param id
     * @param name
     * @param path
     */
    public SourceFile(ParamGroup params, String id, String name, String path) {
        super(params, id, name);
        this.path = path;
    }

    /**
     * Constructor to the MzMl File Source
     * @param cvParams
     * @param userParams
     * @param id
     * @param name
     * @param path
     */
    public SourceFile(List<CvParam> cvParams, List<UserParam> userParams, String id, String name, String path) {
        super(cvParams, userParams, id, name);
        this.path = path;
    }

    /**
     * Constructor of MzIdentMl source File
     * @param id
     * @param name
     * @param path
     * @param fileFormat
     * @param externalFormatDocumentationURI
     */
    public SourceFile(String id,
                      String name,
                      String path,
                      CvParam fileFormat,
                      String externalFormatDocumentationURI) {
        super(id, name);
        this.path = path;
        this.fileFormat = fileFormat;
        this.externalFormatDocumentationURI = externalFormatDocumentationURI;
    }

    /**
     * Constructor of MzIdentMl source File
     * @param params
     * @param id
     * @param name
     * @param path
     * @param fileFormat
     * @param externalFormatDocumentationURI
     */
    public SourceFile(ParamGroup params,
                      String id,
                      String name,
                      String path,
                      CvParam fileFormat,
                      String externalFormatDocumentationURI) {
        super(params, id, name);
        this.path = path;
        this.fileFormat = fileFormat;
        this.externalFormatDocumentationURI = externalFormatDocumentationURI;
    }

    /**
     * Constructor of MzIdentMl source File
     * @param cvParams
     * @param userParams
     * @param id
     * @param name
     * @param path
     * @param fileFormat
     * @param externalFormatDocumentationURI
     */
    public SourceFile(List<CvParam> cvParams,
                      List<UserParam> userParams,
                      String id,
                      String name,
                      String path,
                      CvParam fileFormat,
                      String externalFormatDocumentationURI) {
        super(cvParams, userParams, id, name);
        this.path = path;
        this.fileFormat = fileFormat;
        this.externalFormatDocumentationURI = externalFormatDocumentationURI;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public CvParam getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(CvParam fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getExternalFormatDocumentationURI() {
        return externalFormatDocumentationURI;
    }

    public void setExternalFormatDocumentationURI(String externalFormatDocumentationURI) {
        this.externalFormatDocumentationURI = externalFormatDocumentationURI;
    }
}