  * [About mzGraph Browser](#About_mzGraph_Browser.md)
  * [Getting mzGraph Browser](#Getting_mzGraph_Browser.md)
  * [Using mzGraph Browser](#Using_mzGraph_Browser.md)
  * [API Reference](#API_Reference.md)
  * [FAQs](#FAQs.md)
  * [Getting Help](#Getting_Help.md)
  * [Source Code](#Source_Code.md)
  * [Screenshots](#Screenshots.md)

## About mzGraph Browser ##
**PRIDE mzGraph Browser** is a library for visualizing and annotating MS spectrum and chromatogram. It includes features like:
  * Zoom in/out.
  * Export peak values.
  * Save/Print spectrum and chromatogram as image.
  * Highlight peak m/z and intensity values.
  * Highlight mass differences.
  * Display fragment ion annotations.
  * Automatic annotation of amino acid identifications.
  * Filtering on ion series.
  * Filtering on annotation series

We believe that this library is both easy to learn and to extend. It can be of great use for developing computational proteomics tools.

This library is developed using Java, it uses both jFreeChart and PRIDE utilities library extensively.

[top of page](PRIDEmzGraphBrowser.md)

---

## Getting mzGraph Browser ##
The zip file in the [downloads section](http://code.google.com/p/pride-toolsuite/downloads/list) contains the **PRIDE mzGraph Browser** jar file and all other required libraries.

### Maven Dependency ###
**PRIDE mzGraph Browser** library can be used in Maven projects, you can include the following snippets in your Maven pom file.
```
 <dependency>
   <groupId>uk.ac.ebi.pride.mzgraph</groupId>
   <artifactId>pride-mzgraph-browser</artifactId>
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

[top of page](PRIDEmzGraphBrowser.md)

---

## Using mzGraph Browser ##
PRIDE mzGraph Browser library gives you Java Swing based components for visualizing and annotating MS spectra and chromatogram.

This library is designed to be integrated into your project easily, and there are two common ways of using it:
  * **As an independent panel**: use this option if you just want to visulize spectrum or chromatogram or you want to program the user interactions yourself.
  * **As a panel with a build-in tool bar**: this option provides you with the main visualize panel, in addition, you will also get a out-box tool bar which can be customized to include your own actions.

#### Spectrum Panel ####
The starting point in using PRIDE mzGraph library to visualize spectrum is to create a `SpectrumPanel`. This panel can be added as a component to Java Swing container, and it is the basis of all user interactions.

The following code shows you how to create an instance of the `SpectrumPanel` and add it to the Swing component in your project:
```
// Create a m/z data array
double[] mzArr = new double[]{1.0, 2.012312313, 3.0, 4.234, 6.0, 7.34342};
// Create an intensity data array
double[] intentArr = new double[]{2.0, 4.345345345, 6.0, 1.4545, 5.0, 8.23423};
// Create a spectrum panel
SpectrumPanel spectrum = new SpectrumPanel(mzArr, intentArr);
// Paint the spectrum peaks
spectrum.paintGraph();
// Added the spectrum panel to your own JPanel
JPanel container = new JPanel(new BorderLayout());
container.add(spectrum, BorderLayout.CENTER);
```

Only an array of m/z values and an array of intensity values are required to build a spectrum. You can also overwrite this peak list with a new one, the code below shows you how:
```
// New m/z array
double[] newMz = new double[]{2.0, 3.0, 12.23, 1.45};
// New intensity array
double[] newIntent = new double[]{45, 67, 18.34, 34.78};
// Set a new peak list
spectrum.setPeakList(newMz, newIntent);
```

After the spectrum has been initialized, you can annotate it with fragment ion information. Below is an example of adding a b ion and a y ion:
```
// Create a new y ion with charge -2 and location 2 as well as a water loss
IonAnnotationInfo yIonInfo = new IonAnnotationInfo();
// Create and add an annotation item which describes the ion.
IonAnnotationInfo.Item yIonItem = new IonAnnotationInfo.Item(-2, FragmentIonType.Y_ION, 2, NeutralLoss.WATER_LOSS);
yIonInfo.addItem(yIonItem);
// Create the y ion
IonAnnotation yIon = new IonAnnotation(2.0, 45, yIonInfo);

// Create a new b ion with charge +1 and location 3
IonAnnotationInfo bIonInfo = new IonAnnotationInfo();
IonAnnotationInfo.Item bIonItem = new IonAnnotationInfo.Item(1, FragmentIonType.B_ION, 3, null);
bIonInfo.addItem(bIonItem);
IonAnnotation bIon = new IonAnnotation(12.23, 18.34, bIonInfo);

// Add these ions to the spectrum
List<IonAnnotation> ions = new ArrayList<IonAnnotation>();
ions.add(ion1);
ions.add(ion2);
spectrum.addFragmentIons(ions);
```
If several ions of the same series are added, `SpectrumPanel` will try to assign amino acid annotations between the ion peaks. However, if the identified peptide has post translational modifications(PTM), you will need to let `SpectrumPanel` know the length of the peptide as well as the modification details. The code below shows you how, assuming the length of the peptide is `8` and a list of PTMs are stored in `modifications`:
```
// Set the length of peptide and PTMs as annotation parameters
spectrum.setAminoAcidAnnotationParameters(8, modifications);
spectrum.addFragmentIons(ions);
```

#### Spectrum Panel with a Build-in Tool Bar ####
`SpectrumBrowser` is an extension of `SpectrumPanel`, it uses `SpectrumPanel` to visualize spectrum, it also added a expandible tool bar to perform some common actions. For instance, save/print spectrum as an image, hide the entire peak list and clear all the highlighted mass differences. More importantly, it provides a annotation panel which can filter based on ion series and amino acid annotation series.

You can also add your own component or actions to the build-in tool bar. For instance, if you have a general description panel related to a spectrum, and you would like to add it to the tool bar, the code below shows you how:
```
// Create a new SpectrumBrowser
SpectrumBrowser browser = new SpectrumBrowser();
// Set the spectrum peak list
browser.setPeakList(mzArray, intensityArray);
// Add fragment ions
browser.addFragmentIons(ions);

// Create a general description panel
JPanel descPanel = new JPanel();
// Add the panel to SpectrumBrowser
browser.add(icon, label, tooltip, actionCommand, descPanel);
```
This code will add a new button the tool bar, the general description panel will show/hide when you click on the button.

[top of page](PRIDEmzGraphBrowser.md)

---

## API Reference ##
To come in the future.

[top of page](PRIDEmzGraphBrowser.md)

---

## FAQs ##
To come in the future.

[top of page](PRIDEmzGraphBrowser.md)

---

## Getting Help ##
If you have questions or need additional help, please contact the PRIDE Helpdesk at the EBI: **pride-support at ebi.ac.uk (replace at with @)**.

Please send us your feedback, including error reports, improvement suggestions, new feature requests and any other things you might want to suggest to the PRIDE team.

[top of page](PRIDEmzGraphBrowser.md)

---

## Source Code ##
To come in the future.

[top of page](PRIDEmzGraphBrowser.md)

---

## Screenshots ##
(Click on figure to see the full size version)

<table><tr>
<td><a href='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-mzgraph-browser/fragment_ions.png'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-mzgraph-browser/fragment_ions_small.png' /></a></td>
<td><a href='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-mzgraph-browser/zoom_in.png'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-mzgraph-browser/zoom_in_small.png' /></a></td>
<td><a href='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-mzgraph-browser/tooltip.png'><img src='http://pride-toolsuite.googlecode.com/svn/wiki/images/screenshots/pride-mzgraph-browser/tooltip_small.png' /></a></td>
</tr>
</table>

[top of page](PRIDEmzGraphBrowser.md)