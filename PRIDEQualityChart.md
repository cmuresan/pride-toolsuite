  * [About PRIDE Quality Chart](#About_PRIDE_Quality_Chart.md)
  * [Getting PRIDE Quality Chart](#Getting_PRIDE_Quality_Chart.md)
  * [Using PRIDE Quality Chart](#Using_PRIDE_Quality_Chart.md)
  * [API Reference](#API_Reference.md)
  * [FAQs](#FAQs.md)
  * [Getting Help](#Getting_Help.md)
  * [Source Code](#Source_Code.md)

## About PRIDE Quality Chart ##

The purpose of PRIDE Quality Chart library is to provide a tool for creating charts to assess the quality of your MS experiments. Currently, the library provides eight different charts:
  1. **Peak Intensity Distribution Chart**: A histogram of ion intensity vs frequency for all MS2 spectra in a single PRIDE experiment.
  1. **Precursor Ion Charge Distribution Chart**: Displays a bar chart of precursor ion charge for a single PRIDE experiment.
  1. **MS2 m/z Distribution Chart**: Displays a frequency distribution of product ion m/z for different precursor ion charges.
  1. **Distribution of Precursor Ion Masses Chart**: Displays a frequency distribution of product ion m/z for different precursor ion charges.
  1. **Number of Peptides Identified per Protein Chart**: Displays a bar chart with the number of peptides identified per protein for a single PRIDE experiment.
  1. **Number of Peaks per Spectrum Chart**: Displays a histogram of number of peaks per MS/MS spectrum in a single PRIDE experiment.
  1. **Delta m/z Chart**: Displays a relative frequency distribution of theoretical precursor ion mass - experimental precursor ion mass.
  1. **Number of Missed Tryptic Cleavages Chart**: Display the number of missed tryptic cleavages.

Note: the library is still evolving, we are committed to expand this library and add more useful charts.

PRIDE Quality Chart library is currently used by one project:
  * [PRIDE Inspector](PRIDEInspector.md)

This library is written in Java, brought to you by the PRIDE team.

[top of page](PRIDEQualityChart.md)

---

## Getting PRIDE Quality Chart ##
The zip file in the [downloads section](http://code.google.com/p/pride-toolsuite/downloads/list) contains the **PRIDE Quality Chart** jar file and all other required libraries.

### Maven Dependency ###
**PRIDE Utilities** library can be used in Maven projects, you can include the following snippets in your Maven pom file.
```
 <dependency>
   <groupId>uk.ac.ebi.pride.chart</groupId>
   <artifactId>pride-chart</artifactId>
   <version>0.1.0</version>
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

[top of page](PRIDEQualityChart.md)

---

## Using PRIDE Quality Chart ##
Here we will show you how to use the PRIDE Quality Chart library to create all the charts associated to a PRIDE Experimet Accession Number (stored in the public PRIDE-database), how to store the intermediate data and how to reuse it instead of calculated it again.

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
    //the default configuration in your `settings.xml` file to access to de database
    ExperimentSummaryData summaryData = new PrideChartSummaryData(accessionNumber);
    listOfCharts = PrideChartFactory.getAllCharts(spectralSummaryData);
} catch (SpectralDataPerExperimentException e) {
    listOfCharts = new ArrayList<PrideChart>(); //An empty list
    //Treat the exception
}
//If everything was fine, here listOfCharts contains the available charts
```

The `PrideChartFactory` object can be found in the **uk.ac.ebi.pride.chart.graphics.implementation** package.

[top of page](PRIDEQualityChart.md)

---

#### Store the intermediate data ####
In order to visualize the data in the future without having to wait for the summarization process done by `PrideChartSummaryData` object, the PRIDE Quality Chart library allows you to store an intermediate data in JSon format.

```
///Supose the `listOfCharts` object previously loaded
for(PrideChart prideChart : listOfCharts){
    //jsonData will contain the neccessary data to create the chart
    String jsonData = prideChart.getChartJsonData();
    //type will contain the identifier associated to the PrideChart Object
    int type = PrideChartFactory.getPrideChartIdentifier(prideChart);
    //Store the string associated to the type in a file in order to use it in the future
}
```

The `PrideChartFactory` object can be found in the **uk.ac.ebi.pride.chart.graphics.implementation** package.

[top of page](PRIDEQualityChart.md)

---

#### Reuse the intermediate data ####
If you have the json data stored with the associated type (PrideChart Object identifier), in order to have your chart again, you only need the next code:

```
//Supose the data is in a variable `jsonData` (as String) and the type in `type` (as Integer)
PrideChart prideChart = PrideChartFactory.getChart(type, jsonData);
```

The `PrideChartFactory` object can be found in the **uk.ac.ebi.pride.chart.graphics.implementation** package.

[top of page](PRIDEQualityChart.md)

---

## API Reference ##
To come in the future

[top of page](PRIDEQualityChart.md)

---

## FAQs ##
To come in the future

[top of page](PRIDEQualityChart.md)

---

## Getting Help ##
If you have questions or need additional help, please contact the PRIDE Helpdesk at the EBI: **pride-support at ebi.ac.uk (replace at with @)**.

Please send us your feedback, including error reports, improvement suggestions, new feature requests and any other things you might want to suggest to the PRIDE team.

[top of page](PRIDEQualityChart.md)

---

## Source Code ##

[top of page](PRIDEQualityChart.md)