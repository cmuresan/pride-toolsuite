  * [About PRIDE Chart](#About_PRIDE_Chart.md)
  * [Getting PRIDE Chart](#Getting_PRIDE_Chart.md)
  * [Using PRIDE Chart](#Using_PRIDE_Utilities.md)
  * [API Reference](#API_Reference.md)
  * [FAQs](#FAQs.md)
  * [Getting Help](#Getting_Help.md)
  * [Source Code](#Source_Code.md)

## About PRIDE Chart ##

The purpose of PRIDE Chart library is to provide a tool for creating a set of chart in order to show the quality of the data in everyone of the PRIDE Expermients.

Currently, the library provides eight different charts:
  1. **Peak Intensity Distribution Chart**: A histogram of ion intensity vs frequency for all MS2 spectra in a single PRIDE experiment.
  1. **Precursor Ion Charge Distribution Chart**: Displays a bar chart of precursor ion charge for a single PRIDE experiment.
  1. **MS2 m/z Distribution Chart**: Displays a frequency distribution of product ion m/z for different precursor ion charges.
  1. **Distribution of Precursor Ion Masses Chart**: Displays a frequency distribution of product ion m/z for different precursor ion charges.
  1. **Number of Peptides Identified per Protein Chart**: Displays a bar chart with the number of peptides identified per protein for a single PRIDE experiment.
  1. **Number of Peaks per Spectrum Chart**: Displays a histogram of number of peaks per MS/MS spectrum in a single PRIDE experiment.
  1. **Delta m/z Chart**: Displays a normalised frequency distribution of theoretical precursor ion mass - experimental precursor ion mass.
  1. **Number of Missed Tryptic Cleavages Chart**: .

Note: the library is still evolving, we are committed to expand this library and add more useful charts.

PRIDE Chart library is currently used by one project:
  * [PRIDE Inspector](PRIDEInspector.md)

This library is written in Java, brought to you by the PRIDE team.

[top of page](PRIDEChart.md)

---

## Getting PRIDE Chart ##
The zip file in the [downloads section](http://code.google.com/p/pride-toolsuite/downloads/list) contains the **PRIDE Chart** jar file and all other required libraries.

### Maven Dependency ###
**PRIDE Utilities** library can be used in Maven projects, you can include the following snippets in your Maven pom file.
```
 <dependency>
   <groupId>uk.ac.ebi.pride.chart</groupId>
   <artifactId>pride-chart</artifactId>
   <version>0.0.20</version>
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

[top of page](PRIDEChart.md)

---

## Using PRIDE Chart ##
Here we will show you how to use the PRIDE Chart library to create all the charts associated to a PRIDE Experimet Accession Number (stored in the public PRIDE-database), how to store the intermediate data and how to reuse it instead of calculated it again.

#### Creating all the charts associated to a PRIDE Experimet Accession Number ####
Note. If you've download the code from the SVN, make sure that you've configured correctly the database access. If you're using the project as a library, then the connection to the database is already configured.

You can find the class `PrideChartSummaryData` for calculating the experiment summary data in **uk.ac.ebi.pride.chart.controller** package. It requires one input parameter:
  * `accessionNumber` is the PRIDE Experiment Accession Number in `String`.

The following lines of code shows you how:
```
//The PRIDE Experiment Accession Number we are interested in
String accessionNumber = "9759";

//The future list of PrideChart
List<PrideChart> listOfCharts;
try {
    //Using PrideChartSummaryData only with the accesion number STRING, it will use
    ExperimentSummaryData summaryData = new PrideChartSummaryData(accessionNumber);
    listOfCharts = PrideChartFactory.getAllCharts(spectralSummaryData);
} catch (SpectralDataPerExperimentException e) {
    listOfCharts = new ArrayList<PrideChart>(); //An empty list
    //Treat the exception
}
//If everything was fine, here listOfCharts contains the charts associated
```

#### Store the intermediate data ####

[top of page](PRIDEChart.md)

---


#### Reuse the intermediate data ####

[top of page](PRIDEChart.md)

---


## API Reference ##
To come in the future

[top of page](PRIDEChart.md)

---

## FAQs ##
To come in the future

[top of page](PRIDEChart.md)

---

## Getting Help ##
If you have questions or need additional help, please contact the PRIDE Helpdesk at the EBI: **pride-support at ebi.ac.uk (replace at with @)**.

Please send us your feedback, including error reports, improvement suggestions, new feature requests and any other things you might want to suggest to the PRIDE team.

[top of page](PRIDEChart.md)

---

## Source Code ##

[top of page](PRIDEChart.md)