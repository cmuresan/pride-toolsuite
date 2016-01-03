## Quick Download ##
Download the latest release: [Download](http://www.ebi.ac.uk/pride/resources/tools/inspector/latest/desktop/pride-inspector.zip)

---


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


<table>
<blockquote><tr>
<blockquote><td width='70%'>
</blockquote></blockquote><ul><li><a href='#About_PRIDE_Inspector.md'>About PRIDE Inspector</a>
</li><li><a href='#Getting_PRIDE_Inspector.md'>Getting PRIDE Inspector</a>
<ul><li><a href='#Installation_Requirements.md'>Installation Requirements</a>
</li><li><a href='#Launch_via_Webstart.md'>Launch via Webstart</a>
</li><li><a href='#Download.md'>Download</a>
</li></ul></li><li><a href='#PRIDE_Inspector_Publication.md'>PRIDE Inspector Publication</a>
</li><li><a href='#Disclaimer.md'>Disclaimer</a>
</li><li><a href='#FAQs.md'>FAQs</a>
</li><li><a href='#Getting_Help.md'>Getting Help</a>
</li><li><a href='#Source_Code.md'>Source Code</a>
</li><li><a href='#Screenshots.md'>Screenshots</a>
</li><li><a href='Journal_review.md'>Journal review using PRIDE Inspector</a>
<blockquote></td>
</blockquote><blockquote></tr>
</table></blockquote></li></ul>


---

## About PRIDE Inspector ##
The key features include:
  * Fast loading of **mzML**, **PRIDE XML** and **mzIdentML** files.
  * Search, access and download all PRIDE public database experiments.
  * Different views on spectra, chromatogram, protein, peptides and metadata.
  * Visualise quantification data for both protein and peptide identifications.
  * Experiment summary on key measurements of experiment quality.
  * Download additional protein details, such as: protein name, sequence.
  * Visualise protein sequences and their peptide/PTM coverage.
  * Visualisation for all spectra and chromatograms, including automatic MS2 fragment ion annotations.
  * Possibility to perform a quality assessment of the data using a statistical view with different charts.
  * User-friendly download facility for **private** PRIDE experiments.

The data formats supported by PRIDE Inspector are:
  * **[mzML 1.1](http://www.psidev.info/index.php?q=node/257)**
  * **[PRIDE XML 2.1](http://www.ebi.ac.uk/pride/resources/schema/pride/doc/pride.html)**
  * **[mzIdentML 1.1.0](http://www.psidev.info/mzidentml)**


---


## Getting PRIDE Inspector ##

### Installation Requirements ###
  * **Java**: Java JRE 1.6(or above), which you can download for free [here](http://java.sun.com/javase/downloads/index.jsp). (Note: most computers should have Java installed already).
  * **Operating System**: The current version has been tested on Windows 7, Windows Vista, Linux and Max OS X, it should also work on other platforms. If you come across any problems on your platform, please contact the PRIDE Help Desk.
  * **Memory**: MS dataset can be very large sometimes, in order to get good performance from PRIDE Inspector, we recommend you to have 1G of free memory.

[top of page](PRIDEInspector.md)


---

### Launch via Webstart ###
> Click <a href='http://www.ebi.ac.uk/pride/resources/tools/inspector/latest/webstart/pride-inspector.jnlp'>here</a> to launch directly the latest **PRIDE Inspector**.

Please note that Mac OS 10.8 (Mountain Lion) users or users of the Google Chrome browser, may have to execute additional steps. Please see FAQ section below if in doubt.

[top of page](PRIDEInspector.md)


---


### Download ###
You can get the latest **PRIDE Inspector** from our [Download Section](http://www.ebi.ac.uk/pride/resources/tools/inspector/latest/desktop/pride-inspector.zip), and download `pride-inspector-X.Y.zip` (where X and Y represent the version of the software). Unzipping the file, creates the following directory structure:
```
  pride-inspector-X.Y
     pride-inspector-X.Y.jar
     log
     lib
     examples
     config
```
To start the software, simply double-click the file named `pride-inspector-X.Y.jar`.
If this fails, try to download and install Java 1.5 or above, as explained in the previous section. (The program can also be started from the command line using the following command: `java -jar pride-inspector-X.Y.jar`.)

The zip file contains also an `examples` folder with 2 sample files: one in mzML format (`mzml-example.mzML`) and the other in PRIDE xml format (`pride-example.xml`) so you can upload them in pride inspector and try the application. There is and additional folder, `config`, that contains a file called `config.props` where you can modify the amount of memory assigned to your application (only change if you are trying to view files and is causing the software crash because of a "Out of memory..." exception). The additional 2 directories, `lib` and `log`, contain all the java libraries necessary for the application to run and some debugging information if the application crashes.

[top of page](PRIDEInspector.md)


---

## PRIDE Inspector Publication ##

When you use PRIDE Inspector, please cite the following publication:

  * **[Wang et al., Nat Biotechnol 2012; 30:130-132](http://www.nature.com/nbt/journal/v30/n2/full/nbt.2112.html). [PDF File](http://www.nature.com/nbt/journal/v30/n2/pdf/nbt.2112.pdf).  [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/22318026).**


[top of page](PRIDEInspector.md)


---



## Disclaimer ##
  1. **Disclaimer on shared peptides problem**
> Please be aware that usually only one of the possible peptide to protein mappings are kept in PRIDE. In shotgun proteomics experiments, protein inference needs to be taken into account when interpreting protein identifications reported that are based on non-ambiguous (or shared) peptides.


[top of page](PRIDEInspector.md)


---


## FAQs ##
  1. **What to do if PRIDE Inspector runs out of memory?**
> Although PRIDE Inspector is design to handle large datasets with small memory footprint, it can still run out of memory if many files or public experiments are opened at the same time. If you have seen such a warning message in a popup window, you should try to increase the memory allocation for the program.This can be done by editing the `config.props` file, this file is located in the `config` folder of the installation directory. In the file, change the `pride.inspector.max.memory=1024` to a higher number, for instance, `pride.inspector.max.memory=2048` will give PRIDE Inspector 2G of memory. Please note that you will need to restart PRIDE Inspector in order for the change to take effect. It is also important to know that on a 32-bit operating system you can not increase this value beyond 2000.

2. **PRIDE Inspector does not automatically start when using Google Chrome**
> There is a limitation with Google Chrome. It does not automatically start WebStart applications, instead it downloads the JNLP file and the user will have to manually execute (click) it.

3. **My Mac complains about security and uncertified applications when I try to run the PRIDE Inspector with the Web Start option**
> Apple has introduced tighter security settings (since version 10.8, Mountain Lion), which by default don't allow applications loaded over the internet to run if they are not from the Apple Store or from an Apple certified programmer. There are currently issues certifying Java Web Start applications and we are working to find a solution. In the meantime, users can change the security settings in their Mac.
> Go to: Systems Preferences -> Security & Privacy -> General tab and change the "Allow applications downloaded from" setting from the default "Mac App Store and identified developers" to "Anywhere".
> More information can be found [here](http://support.apple.com/kb/HT5290).

> Note: Java has it's own security checks and the PRIDE Inspector is properly equipped with recognised security certificates. Unfortunately Apple does not recognise them for their new security feature.

[top of page](PRIDEInspector.md)


---


## Getting Help ##
If you have questions or need additional help, please contact the PRIDE Helpdesk at the EBI: **pride-support at ebi.ac.uk** (replace at with @). To help us better identify the problem, you can also include a (preferably zipped) copy of the `pride-inspector.log` from the `log` folder.

[top of page](PRIDEInspector.md)


---


## Source Code ##

You can browse the source code here:
[Browse code repository](http://code.google.com/p/pride-toolsuite/source/browse/#svn%2Fpride-inspector%2Ftrunk)

Alternatively, non-members may check out a read-only working copy anonymously over HTTP:
```
svn checkout http://pride-toolsuite.googlecode.com/svn/pride-inspector/trunk pride-inspector-read-only
```

[top of page](PRIDEInspector.md)


---


## Screenshots ##
(Click on a screenshot to see the full size version)
<table><tr>
<td><a href='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/database_search.png'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/database_search_small.png' /></a></td>
<td><a href='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/general_tab.png'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/general_tab_small.png' /></a></td>
<td><a href='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/protein_tab.png'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/protein_tab_small.png' /></a></td>
<td><a href='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/peptide_tab.png'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/peptide_tab_small.png' /></a></td>
</tr>
<tr>
<td><a href='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/spectrum_tab.png'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/spectrum_tab_small.png' /></a></td>
<td><a href='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/chart_tab.png'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/chart_tab_small.png' /></a></td>
<td><a href='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/quantification_tab.png'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-inspector/quantification_tab_small.png' /></a></td>
</tr>
</table>

[top of page](PRIDEInspector.md)