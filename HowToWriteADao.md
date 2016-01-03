


---


# Introduction #

Data access objects (DAOs) in PRIDE Converter 2 are the components used for accessing MS result files in different file formats. For every file format there must be one DAO supporting it. All DAOs implement the _DAO_ interface and thereby unify the heterogenous data coming form the various input formats and facilitating the integration of new data formats into the PRIDE Converter 2.


---


# General Concepts #

This section describes some general concepts of the PRIDE Converter 2 project. Please do read this section before starting to write your DAO.

## Conversion modes ##

PRIDE Converter 2 works in two conversion modes: the "pre-scan" and the "convert" mode, where the first is mainly concerned about meta-data and annotation of the data and the second mode handles that actual data. Generally, the available input files do not contain all the meta-data required to create valid PRIDE XML files (and hence has to be extended by the converter).

The **pre-scan mode** is triggered first and used to collect all available meta-data from the input file to determine the level of existing annotation and the missing values that will have to be provided by the user. Most DAO functions are only called in _pre-scan mode_. The meta-data is stored by the converter for the later conversion process.

The **conversion mode** is the mode where the actual conversion is being done. The actual conversion process uses mainly the report file (stored meta-data) generated from the pre-scan mode as input and only accesses the DAO to retrieve actual data or non-meta-data (such as spectra data and fragment ion annotations).

## The dao-api package ##

The dao-api package contains the basic _DAO_ interface as well as the _AbstractDAOImpl_ class that need to be implemented / extended by every DAO. Furthermore, it contains several general purpose classes used by all DAOs.

A detailed description of the DAO interface can be found in the next sections below. First, a description of the general purpose classes found in the dao-api package.

### DAOCvParams ###

This enum provides a list of _cvParams_ used by all DAOs. DAOs should generally not contain any hard coded _cvParams_ but only use the ones provided in the _DAOCvParams_ enum. In case a required _cvParam_ is not present in the enum it should be added by contacting a core developer of the PRIDE Converter project. The _DAOCvParams_ enum provides several helper functions that automatically generate PRIDE JAXB _CvParam_ objects as well as ReportFile _CvParam_ objects.

### QuantitationCvParams ###

To make the organization of _cvParams_ a little easier all quantiation related cvParams can be found in the _QuantitationCvParams_ enum. The _cvParams_ currently found here are temporary as they are all from the PRIDE ontology. This will be changed as soon as the required _cvParams_ are added to the MS ontology.

### DAOProperty ###

This class is used by the DAOs to let the PRIDE Converter framework know which options they support. A detailed description of this class can be found at the description of the _GetSupportedProperties_ function.

### Utils ###

The _Utils_ class provides general purpose functions used in several DAOs. All functions of the _Utils_ class are static. Detailed descriptions about the functions of the _Utils_ class can be found in the corresponding JavaDoc.


---


# Writing a DAO #

## 1.) Maven project setup ##

Every DAO is developed as a single maven project. The project is generally called dao-[[fileformat](fileformat.md)]-impl (for example _dao-mascot-impl_ or _dao-mzidentml-impl_).

When setting up the project add the following dependencies and maven repositories to your DAO project's pom.xml:
```
<dependency>
    <groupId>uk.ac.ebi.pride.tools.converter</groupId>
    <artifactId>dao-api</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

```
<repository>
    <id>ebi-repo</id>
    <name>The EBI internal repository</name>
    <url>http://www.ebi.ac.uk/~maven/m2repo</url>
</repository>
<repository>
    <id>ebi-snapshot-repo</id>
    <name>The EBI internal snapshot repository</name>
    <url>http://www.ebi.ac.uk/~maven/m2repo_snapshots</url>
</repository>
```

## 2.) Main DAO class ##

Every DAO consists of one main class that is used by the PRIDE Converter framework. This class is generally called [[fileformat](fileformat.md)]Dao (for example _MascotDao_).

The DAO class **must** implement the _DAO_ interface as well as extend the _AbstractDAOImpl_ class.

```
public class YourDao extends AbstractDAOImpl implements DAO {

// your functions...
}
```

Detailed descriptions about every function of the _DAO_ Inteface can be found in the next section.

## 3.) Adding required static method ##

Every DAO must overwrite the _AbstractDAOImpl_'s getSupportedPorperties function. This method returns a Collection of _DAOProperty_ describing the various specific configuration options for the DAO. These options are automatically exposed through the command line interface as well as the GUI component.

One example of such an option is Mascot's minimum probability setting:
```
DAOProperty<Double> minProbability = new DAOProperty<Double>(
  "min_probability", // the property's name - must only contain [A-Za-z0-9_]
  0.05,              // the property's default value (if applicable)
  0.0,               // the property's minimum value (if applicable)
  1.0);              // the property's maximum value (if applicable)

minProbability.setDescription(
  "Specifies a cut-off point for protein scores, a cut-off for an Integrated error tolerant search and a threshold for calculating MudPIT scores. This value represents a probability threshold."
);
```

## 4.) DAO Implementation Hints ##

The process of implementing a DAO primarily consists of instantiating the entities contained in the package 'uk.ac.ebi.pride.tools.converter.report.model' using the information contained in the source(s) file(s) to convert from. These entities will be later on returned by the DAO interface when required by the PRIDE converter. We can see how these entities relate with the DAO in the class diagram.

![http://farm8.staticflickr.com/7057/6966837261_dc4660d199_o.png](http://farm8.staticflickr.com/7057/6966837261_dc4660d199_o.png)

Using the entities provided through the DAO interface, the Converter can generates the PrideXML file. Actually, the report.model entities has been generated from the PrideXML schema.

Typically, the files are parsed when the DAO constructor is invoked. Due to potential memory problems when dealing with big files, it is not recommended to keep in memory all these entities at the same time during source file parsing. Instead, an internal DAO-specific representation is recommended, using lightweight objects. These lightweight objects shall be used in the very last moment (when the DAO interface methods are invoked) to generate the report.model entities. The most representative example of this is the getIdentificationsIterator method. A typical implementation implemented its own Iterator and generates each Identification object in the Iterator.next() method, keeping in memory just one model entity.


### Identifications, Peptides, and PTMs ###

PrideXML file format is "protein-oriented". This means that identifications consists of protein elements and their associated peptides. Therefore, the report.model.Identification object represents several entities associated with a protein:

  * Accession and other parameters (e.g. score, threshold).

  * List of Peptides (uk.ac.ebi.pride.tools.converter.report.model.Peptide).

Each Peptide has itself associated more information:

  * AA sequence, spectrum reference, ID inside the spectrum, etc.

  * Additional parameters, including cvParams and userParams.


Again, all this information comes from the source files. It is the DAO's responsibility to extract this information and instantiate the objects properly.

### Spectrum ###


---


# The _DAO_ Interface #

This is the main interface for format-specific parsing. Each implementation is responsible to support as much of desired functionality as possible. It is appreciated that not all formats will make the requested data items available. In such cases, the methods should return null primitives and empty collections.

If information is available and the methods are expected to return Param types, it is valid that the implementations return UserParam objects for terms where the CvParam cannot be explicitely set at runtime. It will be the responsibility of the user to inspect the report file generated and make certain that the information is correct and, if possible, convert the UserParam data into the appropriate CvParams. In any case, the report formats will undergo a validation step where missing or incorrect information will be flagged to the user before the full parsing into PRIDE XML is executed.

The DAO must report all possible protein-to-peptide assignments. External tools will be available to update the report file based on specific protein inference algorithms.

The _DAO_ interface contains optional and required functions. Optional functions may return NULL in case they are not supported by the search engines. Required functions must always return an object of the defined return type and in case the information is not available should return a sensible default value.

## Helper methods ##

### setConfiguration ###

This function is called by the PRIDE Converter framework to pass the DAO specific configuration setting made by the user to the DAO. The Properties names will be the same as set in the _DAOProperty_ objects returned by getSupportedProperties.

### getConfiguration ###

Return the current configuration as a Properties object.

## Pre-scan mode methods ##

### getExperimentTitle (required) ###

Must return some experiment title. In case no title is provided by the search enginge's result file, a default title should be returned.

### getExperimentShortLabel (optional) ###

The experiment short label should be used to identify the experiment internally.

### getExperimentParams (required) ###

As a minimal requirement the date of search and the original MS data file format should be set. A common implementation of this function is
```
// initialize the collection to hold the params
Param params = new Param();

Date searchDate = new Date(msec);
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

params.getCvParam().add(DAOCvParams.DATE_OF_SEARCH.getParam(formatter.format(searchDate)));
params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("Mascot dat file"));

// the type of search performed can also be reported here
if (isPMF())
   params.getCvParam().add(DAOCvParams.PMF_SEARCH.getParam());
if (isMSMS())
   params.getCvParam().add(DAOCvParams.MS_MS_SEARCH.getParam());

// in this example the DAO supports the generation of FDR values
Double fdr = getFDR();
params.getCvParam().add(DAOCvParams.PEPTIDE_FDR.getParam(fdr.toString()));
```

### getSampleName (optional) ###

A human readable name for the analyzed sample. This information will generally not be available in a search engine result file.

### getSampleComment (optional) ###

A human readable description of the analyzed sample. This information will generally not be available in a search engine result file.

### getSampleParams (required) ###

Parameters describing the analyzed sample. As a minimal requirement the sample's species should be returned.

### getSourceFile (required) ###

Returns a SourceFile object representing the processing input file. A common implementation of this function is
```
// initialize the return variable
SourceFile file = new SourceFile();

file.setPathToFile(sourcefile.getAbsolutePath());
file.setNameOfFile(sourcefile.getName());
file.setFileType("Mascot dat file");

return file;
```

### getContacts (optional) ###

A Collection of contacts for the given experiment. This information will generally not be available in a search engine result file.

### getInstrument (optional) ###

Returns parameters describing the instrument configuration used to perform the reported experiment. This information will generally not be available in a search engine result file.

### getSoftware (required) ###

Should contain the search engine's name and version.

### getProcessingMethod (optional) ###

Should describe processing methods used to generate the peak list. Furthermore, common search engine settings such as the used tolerance settings (parent and fragment mass tolerance) as well as possible thresholds and scoring methods should also be reported here.

### getProtocol (optional) ###

Describes the experimental procedures performed during the whole experiment. This information will generally not be available in a search engine result file.

### getReferences (optional) ###

A Collection of _Reference_ objects describing publications presenting this experiment. This information will generally not be available in a search engine result file.

### getSearchDatabaseName (required) ###

The names of the search database(s) used in the experiment. These will be written to the FASTA attributes and will be used in the FASTA section if there are multiple sequence files, the search database name will be a string-delimited concatenation of all the names. Idem for version.

### getSearchDatabaseVersion (required) ###

See getSearchDatabaseName.

### getPTMs (required) ###

Should return a collection of PTMs representing all PTMs that are used in this search. The PTM object should at least contain the SearchEnginePTMLabel, whether they are fixed or variable modifications, the deltas and the Residues.

The **SearchEnginePTMLabel** is used by the DAO to identify the given modification in this function and when reporting modifications for peptides. It is by the converter framework to merge user annotated information about the modification with the one reported by the DAO. The SearchEnginePTMLabel will not be written to the final PRIDE XML file.

The **Residues** string specifies the amino acids as single-letter code on which the given modification was observed (f.e. "CM" for cysteine and methionine). The N-terminus should be reported as "0" and the C-terminus as "1".

### getDatabaseMappings (required) ###

Should return a collection of DatabaseMappings that contain all search database names and versions used in this search. The _DatabaseMapping_ objects should only contain the SearchEngineDatabaseName and SearchEngineDatabaseVersion. This information is the used for the user to curate the reported database name and version.

### getSearchResultIdentifier (required) ###

Returns a _SearchResultIdentifier_ object identifying the input file. A common implementation of this function is
```
/**
 * sourcefile used in this example is a File object
 * representing the passed input file.
 */
// intialize the search result identifier
SearchResultIdentifier identifier = new SearchResultIdentifier();

// format the current time
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

identifier.setSourceFilePath(sourcefile.getAbsolutePath());
identifier.setTimeCreated(formatter.format(new Date(System.currentTimeMillis())));
identifier.setHash(FileUtils.MD5Hash(sourcefile.getAbsolutePath()));

return identifier;
```

### getCvLookup (required) ###

Must return a non-null list of all CV lookups used by the DAO. A common implementation using the PRIDE and the MS ontology looks as follows:
```
ArrayList<CV> cvs = new ArrayList<CV>();

cvs.add(new CV("MS", "PSI Mass Spectrometry Ontology", "1.2", "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
cvs.add(new CV("PRIDE", "PRIDE Controlled Vocabulary", "1.101", "http://ebi-pride.googlecode.com/svn/trunk/pride-core/schema/pride_cv.obo"));

return cvs;
```

## Conversion mode methods ##

### getSpectrumCount (required) ###

Must return a count of the number of spectra. If onlyIdentified is true, returns only count of identified spectra. If false, returns count of all spectra.

### getSpectrumIterator (required) ###

This iterator is the only method used by the PRIDE Converter framework to access the spectra in the input file. If onlyIdentified is set to true the returned iterator should only iterate over identified spectra.

### getSpectrumReferenceForPeptideUID (required) ###

Returns the spectrum reference for the given peptide. The **peptide's unique identifier (peptideUID)** is only used by the DAO to identify peptide objects.

### getIdentificationByUID (required) ###

Returns the _Identification_ object identified by the passed unique identifier. The **identification unique identifier (identifierUID)** is only used by the DAO to keep track of identification object. This identifier is not written to the final PRIDE XML file.

## Shared methods ##

### getIdentificationIterator ###

This method will return an iterator that will return individual identification objects.

In **prescan-mode** the complete Identification and Peptide objects should be returned without the peptide's fragment ion annotation. Peptide items have to contain all the PTMs.

In **conversion-mode** (= !prescanMode) Peptide and Protein objects should **NOT** contain any additional parameters and peptidePTMs should **NOT** be included. Furthermore, the different handlers should also <b>NOT</b> be invoked. Peptide FragmentIon annotations are mandatory (if applicable) in scanMode. The identification iterator may return null for an identification.