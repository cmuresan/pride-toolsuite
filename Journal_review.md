# Introduction #

As an integrated desktop application for visualizing and analysing MS datasets, PRIDE Inspector was created to help data submitters, journal editors, and journal reviewers to visualize, browse and assess proteomics experimental data in PRIDE.

Below we highlight some of the features facilitating the journal reviewing process.  For a full overview of all of the features of PRIDE Inspector and a full user manual, please refer to the PRIDE Inspector journal publication and supplementary information ([Wang et al., Nat Biotechnol 2012; 30:130-132](http://www.nature.com/nbt/journal/v30/n2/full/nbt.2112.html). [PDF File](http://www.nature.com/nbt/journal/v30/n2/pdf/nbt.2112.pdf).  [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/22318026)).

# Journal Reviewing #

After any data submission to PRIDE is finalised, the researchers receive an e-mail the experiment accession list and a URL to enable reviewers/editors access to the private information using PRIDE Inspector.

As an example, we invite you to give it a try with data from Prof. Mike Dunn’s group (University College Dublin), present in PRIDE (McManus et al., 2010, PMID: 20432482). The manuscript related information is now publicly available. However, the original reviewer account is used for demonstration purposes.

## Using the online version ##

To launch PRIDE Inspector with direct access to the dataset mentioned above, you need to have Java Webstart installed.

Please copy/paste the following URL in a Web Browser [online version](http://www.ebi.ac.uk/pride/jsp/pages/jnlp/jnlp.jsp?username=review02342&password=nsqNx4tt), then follow the instructions. This option will automatically download and open the experiments and ready to be reviewed.

The download of the data can take longer depending on the size of the experiments and on your Internet connection. So, please realize that this process can take several minutes to complete.

Please note that If you are using Google Chrome, you will need to double click the downloaded .jnlp file to launch PRIDE Inspector.

If you don't have Java Web Start installed, you can install PRIDE Inspector locally. For detailed instructions please see below.

## Using the desktop version ##

  1. Ensure you have a recent version of Java installed.
  1. Download the PRIDE Inspector application from [PRIDE toolsuite](http://code.google.com/p/pride-toolsuite/) (See "Feature downloads" on the right block. The last version available will appear there).
  1. Once installed, go to that directory in your local computer and double click the corresponding version pride-inspector.jar file. The application will then start.
  1. In the PRIDE Inspector "Welcome" page click on "Private Download".
  1. A window will pop up. There you need to enter your username and password (it would be the same process for a reviewer account), and click on "Login". Please use as an example: Username : **review02342**  Password: **nsqNx4tt**
  1. The experiments that belong to that account will be listed there (see screenshot below). You can now download them. The download of the data can take longer depending on the size of the experiments and on your Internet connection. So, please realize that this process can take several minutes to complete.

![http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/private_download.png](http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/private_download.png)


# Quick data assessment #

PRIDE Inspector offers three quick ways of assessing the experimental data: **Experiment Summary**, **Summary Charts** and **Experiment Overview**

### Experiment Summary ###
The Experiment Summary panel is located on the bottom-left of the PRIDE Inspector, it offers a snapshot view of the experiment, and is the quickest way of spotting if something looks amiss with the dataset. It shows whether protein identifications, peptide identifications and spectra are present and also shows different types of post translational modifications present in the experiment (See screenshot below).

![http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/experiment_summary.png](http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/experiment_summary.png)

### Summary Charts ###
The Summary Charts Tab provides a set of charts showing different representation of the data contained in the selected experiment. The user needs to click on 'Start' on the corresponding Tab to get the charts displayed.

> It offers reviewers a quick way of assess the overall quality of the experiment using:

  * **Peak Intensity Distribution Charge** : A histogram of ion intensity vs frequency for all MS2 spectra in the experiment.
  * **Precursor Ion Charge Distribution** : Displays a bar chart of precursor ion charges.
  * **Average MS/MS spectrum** : Displays the average MS/MS spectrum.
  * **Distribution of Precursor Ion Masses** : Displays a frequency distribution of product ion m/z for different precursor ion charges.
  * **Number of Peptides Identified per Protein** : Displays a bar chart with the number of peptides identified per protein.
  * **Number of Peaks per Spectrum** : Displays a histogram of number of peaks per MS/MS spectrum.
  * **Delta m/z** : Displays a relative frequency distribution of theoretical precursor ion mass - experimental precursor ion mass.
  * **Number of Missed Tryptic Cleavages** : Displays the number of missed tryptic cleavages.

![http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/summary_charts.png](http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/summary_charts.png)

### Experiment Overview ###
The Overview Tab provides a unified view on the metadata of different types of data sources. It is arranged in a series of three panels, each panel showing a specific type of metadata. The type of metadata is indicated by the panel's title. Each panel can be viewed by clicking on the button at the top of the  Overview Tab (See screenshot below).

You can find out more on the overall information about the experiment, sample details, experiment protocol, MS instrument details and software used for data processing.

![http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/experiment_overview.png](http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/experiment_overview.png)


# Detailed data assessment #

> For detailed data assessment of a experiment, we recommend you use Protein Tab, Peptide Tab and Spectrum/Chromatogram Tab. Also a Quantification tab is enabled if the experiment contains protein/peptide quantitation data. Below is a short summary on each Tab's main functionality, for detailed guide, please refer to the help document embedded in the PRIDE Inspector.

### Protein Tab ###
The Protein Tab provides a protein identification-centric view. It displays all identified proteins. For each protein identification, its related peptide spectrum matches are also displayed. Please note that it is possible to sort the table based on most table headings, so it is possible to instantly focus on proteins having very low or very high numbers of peptides, or those having PTM annotations. (See screenshot below)

![http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/protein_tab.png](http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/protein_tab.png)

### Peptide Tab ###
The Peptide Tab provides a peptide-centric view, independent from the protein inference. All identified peptides are displayed in this area, along with their post translational modifications and spectra. (See screenshot below)

![http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/protein_tab.png](http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/protein_tab.png)


### Spectrum/Chromatogram Tab ###

The Spectrum/Chromatogram Tab provides a spectrum/chromatogram-centric view. All spectra and chromatograms (if available) from the data source are displayed in this area. (See screenshot below)

![http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/spectrum_tab.png](http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/spectrum_tab.png)


### Quantification Tab ###

The Quantification Tab provided the information about the peptide/protein quantification values (see screenshot below).

From top to bottom, you can see three main areas of information:
  * **Protein Quantification**: a summary table on the protein quantification.
  * **Peptide Quantification**: displays the peptides identifying the currently selected protein, and their quantification.
  * **Samples**: show all the samples used in the experiment.
  * **Protein Quantification Comparison**: histogram for comparing protein quantification.
  * **Spectrum Browser**: shows the spectrum identifying the currently selected peptide.

![http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/quant_tab.png](http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/journal-review/quant_tab.png)


