# Introduction #

The **XTandem DAO** is based on the _xtandem-parser_ by Muth et al. A detailed documentation of the _xtandem-parser_ can be found at http://code.google.com/p/xtandem-parser.

X!Tandem result files do not contain the complete searched spectra. Files downloaded from the GPMDB only contain pre-processed spectra using the COMMON compression algorithm. Detailed information about the COMMON algorithm can be found at http://www.thegpm.org/common/index.html.

If available the original spectra can be retrieved from the searched peak list file if provided. To enable this option the peak-list file has to be put in the same directory the source X!Tandem file is placed. If the peak-list file was not renamed since the original search spectra are automatically loaded from the peak list file. Currently, the **XTandem DAO** supports spectra in PKL, DTA, MGF, and the internal compressed COMMON format.

# Parameters #

**Spectrum File**: Allows to manually set the path to the spectrum source file. If this property is set any file referenced in the actual X!Tandem file will be ignored.

**Use Internal Spectra**: If this parameter is set to "true" the spectra stored in the X!Tandem file are used irrespective of whether an external peak list file is referenced. These spectra are highly preprocessed and do not properly represent the input spectra. This option should only be used if the original spectra are not available.

# Details #

  * **Missing features**: Currently, protein thresholds are not supported by the **XTandem DAO** as the used threshold is not available in X!Tandem output files.
  * **PTMs**: Post-translational modifications are only reported using the position and the observed mass-delta. No additional information, such as a modification name are provided in X!Tandem result files. Thus, the **XTandem DAO** reports encountered PTMs only based on the observed mass delta and the affected amino-acid.