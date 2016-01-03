# Introduction #

PRIDE Converter can report quantitative data in PRIDE XML files. Unfortunately, this data is generally not present in the search engines' output files. Therefore, it is possible to submit quantitative data to the conversion process using mzTab files.

# Step-by-Step description #

## 1.) Generate skeleton mzTab file ##

PRIDE Converter can be used to generate a skeleton mzTab file that can then be used to add the quantitative data. When using the command line version of PRIDE Converter simply set the mode to "mztab" to generate an mzTab file.
```
java -jar pride-converter-2.0.jar -engine [USED SEARCH ENGINE] -sourcefile [PATH TO FILE] -mode mztab
```

## 2.) Add quantitative data ##

PRIDE Converter expects several fields to be present in a mzTab file to be able to insert the quantitative data.

### Unit ###

  * **[UNIT\_ID](UNIT_ID.md)-quantification\_method**: A cvParam describing the used quantification methods. The used parameter should be child term of PRIDE:0000307.
```
MTD   [UNIT_ID]-quantification-method   [PRIDE,PRIDE:0000313,iTRAQ,]
```
  * **[UNIT\_ID](UNIT_ID.md)-protein-quantification\_unit**: A cvParam describing what unit the reported numbers are in. The used parameter should be a child term of PRIDE:0000392.
```
MTD   [UNIT_ID]-protein-quantification_unit   [PRIDE,PRIDE:0000395,Ratio,]
```
  * **[UNIT\_ID](UNIT_ID.md)-peptide-quantification\_unit**: When reporting quantification values at the peptide level as well this parameter needs to be present as well. The used parameter should be a child term of PRIDE:0000392.
```
MTD   [UNIT_ID]-peptide-quantification_unit   [PRIDE,PRIDE:0000395,Ratio,]
```

### Unit - Subsamples ###

Quantification values are generally associated with subsamples. Any parameter describing the subsamples properties (such as species, tissue, cell\_type, disease) is evaluated by PRIDE Converter and written into the PRIDE XML file but is not mandatory.

  * **[UNIT\_ID](UNIT_ID.md)-[SUB\_ID](SUB_ID.md)-quantitation\_reagent**: The only mandatory field at the subsample level. The cvParam is expected to be a child term of PRIDE:0000324.
```
MTD   [UNIT_ID]-sub[1]-quantitation_reagent   [PRIDE, PRIDE:0000264, iTRAQ reagent 113,]
MTD   [UNIT_ID]-sub[2]-quantitation_reagent   [PRIDE, PRIDE:0000114, iTRAQ reagent 114,]
```

### Table specific values ###

In the protein / peptide / small molecule tables three quantification related values can be reported per subsample: abundance (**required**), standard deviation (optional), and standard error (optional).