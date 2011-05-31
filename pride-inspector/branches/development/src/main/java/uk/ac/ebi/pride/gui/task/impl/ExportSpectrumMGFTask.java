package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Experiment;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.component.utils.SharedLabels;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: dani
 * Date: 18-Oct-2010
 * Time: 10:46:54
 * To change this template use File | Settings | File Templates.
 */
public class ExportSpectrumMGFTask extends AbstractDataAccessTask<Void, Void> {
    private static final Logger logger = LoggerFactory.getLogger(ExportSpectrumDescTask.class);

    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Exporting Spectrum MGF format";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Exporting Spectrum MGF format";
    /**
     * output File
     */
    private String outputFilePath;

    /**
     * Retrieve spectrum data in an MGF file format
     *
     * @param controller     DataAccessController
     * @param outputFilePath file to output the result.
     */
    public ExportSpectrumMGFTask(DataAccessController controller, String outputFilePath) {
        super(controller);
        this.outputFilePath = outputFilePath;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void runDataAccess() throws Exception {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(new File(outputFilePath)));
            // title: COM
            Experiment exp = (Experiment) controller.getMetaData();
            String title = exp.getTitle();
            if (title != null) {
                writer.println("COM=" + title);
            }
            // taxonomy: TAXONOMY
            for (Comparable spectrumId : controller.getSpectrumIds()) {
                writer.println("BEGIN IONS");
                writer.println("TITLE=" + spectrumId);
                Spectrum spectrum = controller.getSpectrumById(spectrumId);
                writer.println("PEPMASS=" + controller.getPrecursorMz(spectrumId));
                //get both arrays
                double[] mzBinaryArray = spectrum.getMzBinaryDataArray().getDoubleArray();
                double[] intensityArray = spectrum.getIntensityBinaryDataArray().getDoubleArray();

                for (int i = 0; i < mzBinaryArray.length; i++) {
                    writer.println(mzBinaryArray[i] + SharedLabels.TAB + intensityArray[i]);
                }
                writer.println("END IONS" + SharedLabels.LINE_SEPARATOR);

                // this is important for cancelling
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                writer.flush();
            }
            writer.flush();

        } catch (DataAccessException e2) {
            String msg = "Failed to retrieve data from data source";
            logger.error(msg, e2);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Export Error");
        } catch (IOException e1) {
            String msg = "Failed to write data to the output file, please check you have the right permission";
            logger.error(msg, e1);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Export Error");
        } catch (InterruptedException e3) {
            logger.warn("Exporting spectrum in MGF format has been interrupted");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return null;
    }
}
