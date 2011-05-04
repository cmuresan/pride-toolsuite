package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 01-Sep-2010
 * Time: 17:36:06
 */
public class ExportSpectrumDescTask extends AbstractDataAccessTask<Void, Void> {
    private static final Logger logger = LoggerFactory.getLogger(ExportSpectrumDescTask.class);

    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Exporting Spectrum Descriptions";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Exporting Spectrum Descriptions";
    /**
     * output File
     */
    private String outputFilePath;

    /**
     * Retrieve a subset of identifications using the default iteration size.
     *
     * @param controller     DataAccessController
     * @param outputFilePath file to output the result.
     */
    public ExportSpectrumDescTask(DataAccessController controller, String outputFilePath) {
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
            writer.println("Spectrum ID\tMS Level\tPrecursor Charge\tPrecursor m/z\tPrecursor Intensity\tSum of Intensity\tNumber of peaks");
            Collection<Comparable> spectrumIds = controller.getSpectrumIds();
            for (Comparable specId : spectrumIds) {
                writer.print(specId);
                writer.print("\t");
                writer.print(controller.getMsLevel(specId));
                writer.print("\t");
                writer.print(controller.getPrecursorCharge(specId));
                writer.print("\t");
                writer.print(controller.getPrecursorMz(specId));
                writer.print("\t");
                writer.print(controller.getPrecursorIntensity(specId));
                writer.print("\t");
                writer.print(controller.getSumOfIntensity(specId));
                writer.print("\t");
                writer.print(controller.getNumberOfPeaks(specId));
                writer.print("\n");

                // this is important for cancelling
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
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
            logger.warn("Exporting spectrum description has been interrupted");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return null;
    }
}
