# The PRIDE Toolsuite Project Just moved to [GitHub](https://github.com/PRIDE-Toolsuite) #

<font color='#ff0000 size='>The PRIDE Toolsuite project has been moved to <strong><a href='https://github.com/PRIDE-Toolsuite'>GitHub</a></strong> and divided in two main projects <a href='https://github.com/PRIDE-Toolsuite'>PRIDE Toolsuite</a> and <a href='https://github.com/PRIDE-Utilities'>PRIDE Utilities</a>, if you have are interested in new releases and the updated code. The libraries and code were organised in</font>:

<table><tr><td width='600' height='100'>
<strong>PRIDE Toolsuite</strong>

<ul><li><a href='http://github.com/PRIDE-Toolsuite/pride-inspector'>PRIDE Inspector</a>
</li><li><a href='http://github.com/PRIDE-Toolsuite/inspector-mzgraph-browser'>PRIDE mzGraph Browser</a>
</li><li><a href='http://github.com/PRIDE-Toolsuite/inspector-quality-chart'>PRIDE Quality Charts</a>
</li><li><a href='http://github.com/PRIDE-Toolsuite/inspector-swing-utils'>PRIDE Inspector Swing Utilities</a></li></ul>

<strong>PRIDE Utilities</strong>

<ul><li><a href='http://github.com/PRIDE-Utilities/ms-data-core-api'>ms-data-core-api</a>
</li><li><a href='http://github.com/PRIDE-Utilities/pride-utilities'>pride-utilities</a></li></ul>

</td><td><a href='http://github.com/PRIDE-Toolsuite'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/googlecode-to-github.png' /></a></td>
</tr></table>


---

<table><tr><td width='600' height='100'>
<b>PRIDE Tool Suite</b> is a collection of tools and libraries to handle Mass Spectrometry (MS) related data. These tools can visualize and perform quality assessment of your MS dataset as well as accessing all the experiments in PRIDE public database. These libraries are a host of programmable APIs which allows you to build highly customized proteomics applications.</td><td><a href='http://www.ebi.ac.uk/pride'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/pride-logo-medium.png' /></a></td>
</tr></table>
Developed and supported by the [PRIDE](http://www.ebi.ac.uk/pride) team.


### Tools ###

---

#### [PRIDE Inspector](http://code.google.com/p/pride-toolsuite/wiki/PRIDEInspector) ####
> PRIDE Inspector is an integrated desktop application for visualizinig and analyzing MS dataset (such as: mzML, PRIDE XML) as well as providing direct access to PRIDE public database. It also allows PRIDE submitters and journal reviewers to download private PRIDE experiments and perform quality checks. <a href='http://www.ebi.ac.uk/pride/resources/tools/inspector/latest/desktop/pride-inspector.zip'><i><b>Download latest release</b></i></a>

#### [PRIDE Converter](http://code.google.com/p/pride-converter/) ####
> PRIDE Converter converts mass spectrometry data from most common data formats into valid PRIDE XML for submission to the publicly available [PRIDE database](http://www.ebi.ac.uk/pride). It presents a convenient, wizard-like graphical user interface, and includes efficient access to the Ontology Lookup Service ([OLS](http://www.ebi.ac.uk/ols)).


### Libraries ###

---

#### [PRIDE mzGraph Browser library](http://code.google.com/p/pride-toolsuite/wiki/PRIDEmzGraphBrowser) ####
> PRIDE mzGraph Browser library lets you visualize MS spectra and chromatogram. This library also provides API for spectrum annotation.

#### [PRIDE Quality Chart library](http://code.google.com/p/pride-toolsuite/wiki/PRIDEQualityChart) ####
> PRIDE Quality Chart library provides a way to assess the quality of your MS experiments. Using this library, a number of quality charts can be generated which can allow quick quality checks on your dataset.

#### [PRIDE XML JAXB library](http://code.google.com/p/pride-toolsuite/wiki/PRIDEXMLJAXB) ####
> PRIDE XML JAXB library is a lightweight JAXB-based implementation of the full PRIDE XML 2.1 format. This library uses XML indexing technique to give both fast parsing of large PRIDE XML files and small memory footprint.

#### [jmzML library](http://code.google.com/p/jmzml/) ####
> jmzML library is an implementation of the full PSI mzML 1.1 standard format based on JAXB. Like PRIDE XML JAXB library, this library also allows both fast parsing of large mzML files and small memory footprint.

#### [jmzIdentML library](http://code.google.com/p/jmzidentml/) ####
> jmzIdentML library is an implementation of the full PSI mzIdentML 1.1 standard format based on JAXB. Following the PRIDE XML JAXB library and the jmzML library, this API also allows both fast parsing of large mzIdentML files and small memory footprint.

#### [XXIndex library](http://code.google.com/p/pride-toolsuite/wiki/XXIndex) ####
> XXIndex library lets you index a XML file quickly for random access.

#### [PRIDE Utilities library](http://code.google.com/p/pride-toolsuite/wiki/PRIDEUtilities) ####
> PRIDE Utilities library contains code shared by many of projects, including the tools and libraries above.