# Introduction #

PRIDE Converter 2 is a complete rewrite of the previous version.

# Supported Formats #

Currently, PRIDE Converter 2 supports the following MS data formats:

| **Format** | **Type** | **Status** |
|:-----------|:---------|:-----------|
| [Mascot .dat](MascotDatDAO.md) | Identifications + Spectra | Tested on 6 submissions |
| [X!Tandem](XTandemDAO.md) | Identifications + (processed spectra) <br>Can process additional peak list files for spectra <table><thead><th> Tested on 1 submission </th></thead><tbody>
<tr><td> <a href='MzIdentMlDAO.md'>mzIdentML</a> </td><td> Identifications + spectra in additional file </td><td> Complete. Not tested </td></tr>
<tr><td> <a href='OmssaDAO.md'>OMSSA</a> </td><td> Identifications + Spectra </td><td> Under development </td></tr>
<tr><td> dta        </td><td> Spectra  </td><td> Complete. Not tested </td></tr>
<tr><td> mgf        </td><td> Spectra  </td><td> Complete. Not tested </td></tr>
<tr><td> ms2        </td><td> Spectra  </td><td> Complete. Not tested </td></tr>
<tr><td> mzML       </td><td> Spectra  </td><td> Complete. Not tested </td></tr>
<tr><td> <a href='MzXmlDAO.md'>mzXML</a> </td><td> Spectra  </td><td> Tested on 1 submission </td></tr>
<tr><td> pkl        </td><td> Spectra  </td><td> Complete. Not tested </td></tr></tbody></table>

<h1>Supported additional data</h1>

Additional data is generally extracted from mzTab files. A "skeleton" mzTab file can be generated using the new PRIDE Converter when setting "mztab" as mode. This generates a basic mzTab file based on the search engine's input file. This file can then be used to add quantitative / gel based data as described below.<br>
<br>
<h2>Special mzTab fields</h2>

Some fields / values can be supplied using defined optional columns. The more "simpler" fields are summarised in the following table:<br>
<br>
<table><thead><th> <b>Column Header</b> </th><th> <b>Level</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> opt_empai            </td><td> Protein      </td><td> The emPAI for the given protein. This value is mapped to the cvParam PRIDE:0000363 "emPAI value". </td></tr>
<tr><td> opt_tic              </td><td> Protein, Peptide </td><td> The Total Ion Count (TIC) for the given protein or peptide. This valus is mapped to the cvParam PRIDE:0000364 "TIC value". </td></tr></tbody></table>

<h3>Quantiative data</h3>

Quantitative data should be reported as defined by the mzTab format specification. This information is then automatically parsed by PRIDE Converter. Detailed information about what kind of data needs to be present can be found in QuantitativeMzTabFiles.<br>
<br>
<h3>Gel-based data</h3>

This section is currently missing.<br>
<br>
<h1>Known Issues</h1>

<ul><li><b>Duplicate protein entries:</b> The Mascot DAO can report indistinguishable accessions for a protein identification. In case a protein's accession is not found in the provided mzTab file but one of the indistinguishable ones is the protein's accession and this indistinguishable accession are replaced. In rare cases PRIDE XML files might already contain an entry with such an accession. This results in two protein entries with the same accession but different peptides in the PRIDE XML file.