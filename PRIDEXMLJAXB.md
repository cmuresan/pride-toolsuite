  * [About PRIDE XML JAXB](#About_PRIDE_XML_JAXB.md)
  * [Getting PRIDE XML JAXB](#Getting_PRIDE_XML_JAXB.md)
  * [Using PRIDE XML JAXB](#Using_PRIDE_XML_JAXB.md)
  * [API Reference](#API_Reference.md)
  * [FAQs](#FAQs.md)
  * [Getting Help](#Getting_Help.md)
  * [Source Code](#Source_Code.md)

## About PRIDE XML JAXB ##
PRIDE XML JAXB library is a library for indexing and parsing [PRIDE XML 2.1](http://www.ebi.ac.uk/pride/schemaDocumentation.do) files. This library **does not** load the whole file into the memory up-front, instead it employs XML indexing technique to index the file on the fly which gives you fast access and small meomory footprint. Additionally, all entities in PRIDE XML file are mapped as objects, and the internal reference between the objects are resolved automatically, this gives you direct access in the object model to entities that are only referenced by ID in the actual XML file.

PRIDE XML JAXB library is written in Java, brought to you by the PRIDE team.

This library uses the following two external libraries:
  * [XXIndex](XXIndex.md): Indexing XML files.
  * [JAXB](https://jaxb.dev.java.net/): Parsing XML snippets into object model.

If you want to see PRIDE XML JAXB library in action, you can try out [PRIDE Inspector](http://code.google.com/p/pride-toolsuite/w/edit/PRIDEInspector).

[top of page](PRIDEXMLJAXB.md)

---

## Getting PRIDE XML JAXB ##
The zip file in the [downloads section](http://code.google.com/p/pride-toolsuite/downloads/list) contains the **PRIDE XML JAXB** jar file and all other required libraries.

### Maven Dependency ###
**PRIDE XML JAXB** library can be used in Maven projects, you can include the following snippets in your Maven pom file.
```
 <dependency>
   <groupId>uk.ac.ebi.pride.jaxb</groupId>
   <artifactId>pride-jaxb</artifactId>
   <version>1.0.4</version>
 </dependency> 
```

```
 <repository>
   <id>ebi-repo</id>
   <name>The EBI internal repository</name>
   <url>http://www.ebi.ac.uk/~maven/m2repo</url>
   <releases>
     <enabled>true</enabled>
   </releases>
   <snapshots>
     <enabled>false</enabled>
   </snapshots>
 </repository>
```

**Note**: you need to change the version number to the latest version.

For developers, the latest source code is available from our [SVN repository](#Source_Code.md).


[top of page](PRIDEXMLJAXB.md)

---

## Using PRIDE XML JAXB ##
You can start using PRIDE XML JAXB once you have included it either on your classpath or your Maven pom file. The section below will show you how to retrieve spectra, identifications and peptides.

#### Starting Point ####
The first step in using the PRIDE XML JAXB library is to create an instance of `PrideXmlReader` object for a given PRIDE XML file, this object should provide all the methods you need to retrieve key information such as: spectra, identifications and peptides.

Assuming the file your want to load is `pride-example.xml`, you can include the following lines in your code:
```
// Create an instance of PrideXmlReader
PrideXmlReader reader = new PrideXmlReader("pride-example.xml");
```
**Note**: This step will start indexing the input PRIDE XML file automatically, the time it takes depends solely on the size of the input file.

#### Getting Spectra ####
You can start querying spectra data once you had your `PrideXmlReader` initialized. In PRIDE XML JAXB library, spectra are referenced by their spectrum ids, which is unique for each spectrum within each input file.

If you want to iterate over all the spectra from the input file, you can use the following lines of code:
```
// Get a list of spectrum ids
List<String> ids = reader.getSpectrumIds();

// Iterate over each spectrum
for(String id : ids) {
  Spectrum spectrum = reader.getSpectrumById(id);
}
```
You can access information stored in a `Spectrum` object just like you would access a normal Java object. For instance, you can include the following lines of code to get the meta data associated with `SpectrumDesc`.
```
// Get spectrum meta data
SpectrumDesc spectrumDesc = spectrum.getSpectrumDesc();
```

#### Getting Identification ####
There are two types of protein identifications: `GelFreeIdentification` or `TwoDimensionalIdentification`. Although it is rare, these two types of identifications can co-exist. This means protein accessions are not unique, the same protein accession may appear many times in the same file. In PRIDE XML JAXB library, we choose to generate unique ids for protein identifications according to their order in the file.

If you want to iterate over all the protein identifications of a certain type, you can include the following lines in your code:
```
// Get a list of GelFreeIdentification ids
List<String> ids = reader.getGelFreeIdentIds();

// Iterate over each identification
for(String id : ids) {
 GelFreeIdentification gelFree = reader.getGelFreeIdentById(id);
}
```

Alternatively, you can simply iterate over all the identifications:
```
// Get a list of Identification ids
List<String> ids = reader.getIdentIds();

// Iterate over each identification
for(String id : ids) {
 Identification ident = reader.getIdentById(id);
} 
```
#### Getting Peptide ####
Since all peptides must be located within an identification, they are indexed according their order within their parent identification. For example, if there are three peptides within a protein identification, then they will be given index: 1, 2 and 3.

To unqiuely locate a peptide, you must combine its index with its parent identification id, the code below shows you how:
```
// Get the number of peptides within an identification
int numOfPeptides = reader.getNumberOfPeptides(identId);

// Iterate over each peptide
for(int i=0; i < numOfPeptides; i++) {
 Peptide peptide = reader.getPeptide(identId, i);
}
```
#### Getting Other Details ####
If you are interested in accessing other meta data information, such as: protocol, references, `PrideXmlReader` also have convenient methods for that.

To get references, you can use the `getReferences()` method:
```
// Get a list of references
List<Reference> references = reader.getReferences();
```
**Note**: An empty list will be returned when there is not reference details in the input PRIDE XML file.

[top of page](PRIDEXMLJAXB.md)

---

## API Reference ##
To come in the future.

[top of page](PRIDEXMLJAXB.md)

---

## FAQs ##
To come in the future.

[top of page](PRIDEXMLJAXB.md)

---

## Getting Help ##
If you have questions or need additional help, please contact the PRIDE Helpdesk at the EBI: **pride-support at ebi.ac.uk (replace at with @)**.

Please send us your feedback, including error reports, improvement suggestions, new feature requests and any other things you might want to suggest to the PRIDE team.

[top of page](PRIDEmzGraphBrowser.md)

---

## Source Code ##
To come in the future.

[top of page](PRIDEmzGraphBrowser.md)