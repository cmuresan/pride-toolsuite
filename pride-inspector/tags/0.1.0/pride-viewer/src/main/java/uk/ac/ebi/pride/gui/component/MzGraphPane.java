package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.jmzml.gui.ChromatogramPanel;
import uk.ac.ebi.jmzml.gui.SpectrumPanel;
import uk.ac.ebi.pride.data.core.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * MzGraphPane visualizes either peak lists or chromatograms
 * User: rwang
 * Date: 19-Apr-2010
 * Time: 11:37:41
 */
public class MzGraphPane extends JPanel {
    private MzGraph mzGraph = null;

    public MzGraphPane() {
        this(null);
    }

    public MzGraphPane(MzGraph mzGraph) {
        this.mzGraph = mzGraph;
        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        if (mzGraph != null) {
            if (mzGraph instanceof Spectrum) {
                initializeSpectrumPane((Spectrum)mzGraph);
            } else {
                initializeChromatogramPane((Chromatogram)mzGraph);
            }
        }
    }

    private void initializeChromatogramPane(Chromatogram chroma) {
        Collection<BinaryDataArray> binaryDataArrs = chroma.getBinaryDataArrays();
        // get x axis array
        BinaryDataArray xBinaryDataArr = (BinaryDataArray)(binaryDataArrs.toArray())[0];
        double[] xArr = xBinaryDataArr.getBinaryDoubleArray();

        // get y axis array
        BinaryDataArray yBinaryDataArr = (BinaryDataArray)(binaryDataArrs.toArray())[1];
        double[] yArr = yBinaryDataArr.getBinaryDoubleArray();

        // get x axis label
        String xLabel = null;
        Collection<CvParam> xCvParams = xBinaryDataArr.getCvParams();
        for(CvParam xCvParam : xCvParams) {
            if (xCvParam.getUnitAcc() != null) {
                xLabel = xCvParam.getName() + " (" + xCvParam.getUnitName() + ")";
            }
        }

        // get y axis label
        String yLabel = null;
        Collection<CvParam> yCvParams = yBinaryDataArr.getCvParams();
        for(CvParam yCvParam : yCvParams) {
            if (yCvParam.getUnitAcc() != null) {
                yLabel = yCvParam.getName() + " (" + yCvParam.getUnitName() + ")";
            }
        }

        JPanel mzPanel = new ChromatogramPanel(xArr, yArr, xLabel, yLabel);
        this.add(mzPanel, BorderLayout.CENTER);
    }

    private void initializeSpectrumPane(Spectrum spectrum) {
        String spectrumId = spectrum.getId();
        Collection<BinaryDataArray> binaryDataArrs = spectrum.getBinaryDataArrays();
        // get mz array
        BinaryDataArray mzBinaryDataArr = (BinaryDataArray)(binaryDataArrs.toArray())[0];
        double[] mzArr = mzBinaryDataArr.getBinaryDoubleArray();

        // get intensity array
        BinaryDataArray intentBinaryDataArr = (BinaryDataArray)(binaryDataArrs.toArray())[1];
        double[] intentArr = intentBinaryDataArr.getBinaryDoubleArray();
        // error checking
        if (mzArr.length < 1 || intentArr.length < 1) {
            // ToDo: binary data not found
        }

        //get precursor
        Collection<Precursor> precursors = spectrum.getPrecursors();
        double precursorMz = 0.0;
        String precursorCharge = "?";
        if (precursors != null && precursors.size() == 1) {
            // ToDo: handle precursors here
        }

        int msLevel = -1;
        // ToDo: handle ms level here

        JPanel mzPanel = new SpectrumPanel(mzArr, intentArr, msLevel, precursorMz, precursorCharge, spectrumId);
        this.add(mzPanel, BorderLayout.CENTER);
    }
}
